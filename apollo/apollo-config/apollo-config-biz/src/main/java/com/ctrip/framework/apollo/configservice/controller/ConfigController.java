/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.ctrip.framework.apollo.configservice.controller;

import com.ctrip.framework.apollo.configservice.controller.timer.AppNamespaceCache;
import com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease.ClientLoadReleaseStrategy4Normal;
import com.ctrip.framework.apollo.configservice.pattern.pool.HeartBeatPool;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Release;
import common.NamespaceBo;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.dto.ApolloConfig;
import framework.apollo.core.dto.ApolloNotificationMessages;
import framework.apollo.tracer.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ctrip.framework.apollo.configservice.util.IpUtils.tryToGetClientIp;
import static com.google.common.base.Strings.isNullOrEmpty;
import static framework.apollo.core.ConfigConsts.NO_APPID_PLACEHOLDER;

@RestController
@RequestMapping("/configs")
public class ConfigController {
    @Autowired
    private ClientLoadReleaseStrategy4Normal clientLoadReleaseStrategy;
    @Autowired
    private AppNamespaceCache appNamespaceCache;
    @Autowired
    private NamespaceUtil namespaceUtil;
    @Autowired
    private HeartBeatPool heartBeatPool;
    @Autowired
    private Gson gson;

    private static final Type configurationTypeReference = new TypeToken<Map<String, String>>() {
    }.getType();

    @RequestMapping(value = "/{appId}/{env}/{clusterName}/{namespace:.+}", method = RequestMethod.GET)
    public ApolloConfig queryConfig4Client(@PathVariable String appId, @PathVariable String clusterName, @PathVariable String env,
                                           @PathVariable String namespace,
                                           @RequestParam(value = "dataCenter", required = false) String dataCenter,
                                           @RequestParam(value = "releaseKey", defaultValue = "-1") String clientSideReleaseKey,
                                           @RequestParam(value = "ip", required = false) String clientIp,
                                           @RequestParam(value = "messages", required = false) String messagesAsString,
                                           HttpServletRequest request, HttpServletResponse response) throws IOException {

        String originalNamespace = namespace;
        namespace = filterAndNormalizeNamespace(appId, namespace);

        if (isNullOrEmpty(clientIp)) {
            clientIp = tryToGetClientIp(request);
        }

        ApolloNotificationMessages clientMessages = ApolloNotificationMessages.buildMessages(messagesAsString);

        List<Release> releases = findReleases4Client(appId, clusterName, env, namespace, dataCenter, clientIp, clientMessages);

        if (releases.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, String.format(
                    "Could not load configurations with appCode: %s, clusterName: %s, appNamespace: %s", appId, clusterName, originalNamespace));
            return null;
        }
        NamespaceBo namespaceBo = NamespaceBo.builder().appCode(appId).env(env).clusterName(clusterName).namespaceName(namespace).build();

        heartBeatReleases(namespaceBo, clientIp, releases);

        //配置是否 有变化
        String mergedReleaseKey = releases.stream().map(Release::getReleaseKey).collect(Collectors.joining(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR));
        if (mergedReleaseKey.equals(clientSideReleaseKey)) {
            // Client side configuration is the same with server side, return 304
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            Tracer.logEvent("Apollo.Config.NotModified", assembleKey(appId, clusterName, originalNamespace, dataCenter));
            return null;
        }


        ApolloConfig apolloConfig = new ApolloConfig(appId, clusterName, originalNamespace, mergedReleaseKey);
        apolloConfig.setConfigurations(mergeReleaseConfigurations(releases));

        return apolloConfig;
    }

    /**
     * 查找该client 的所有的releases
     */
    private List<Release> findReleases4Client(@PathVariable String appId, String clusterName, String env, String namespace, String dataCenter, String clientIp, ApolloNotificationMessages clientMessages) {
        List<Release> releases = Lists.newLinkedList();

        if (!NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
            Release release4Client = clientLoadReleaseStrategy.loadRelease4Client(appId, clientIp, appId, clusterName, env, namespace, dataCenter, clientMessages);
            if (release4Client != null) {
                releases.add(release4Client);
            }
        }

        //if appNamespace does not belong to this appCode, should check if there is a public configuration
        if (isPublicNamespace(appId, namespace)) {
            Release publicRelease = this.findPublicConfig(appId, clientIp, clusterName, env, namespace, dataCenter, clientMessages);
            if (!Objects.isNull(publicRelease)) {
                releases.add(publicRelease);
            }
        }
        return releases;
    }

    private String filterAndNormalizeNamespace(@PathVariable String appId, @PathVariable String namespace) {
        //strip out .properties suffix
        namespace = namespaceUtil.subSuffix4Properties(namespace);
        //fix the character case issue, such as FX.apollo <-> fx.apollo
        namespace = namespaceUtil.fixCapsLook4NamespaceName(appId, namespace);
        return namespace;
    }


    /**
     * 该AppId 的命名空间中没有 appNamespace
     */
    private boolean isPublicNamespace(String appId, String namespaceName) {
        //Every app has an 'application' appNamespace
        if (Objects.equals(ConfigConsts.NAMESPACE_APPLICATION, namespaceName)) {
            return false;
        }

        //if no appCode is present, then no other appNamespace belongs to it
        if (NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
            return true;
        }

        AppNamespace appNamespace = appNamespaceCache.findByAppIdAndNamespace(appId, namespaceName);

        return appNamespace == null;
    }

    private Release findPublicConfig(String clientAppId, String clientIp, String clusterName, String env,
                                     String namespace, String dataCenter, ApolloNotificationMessages clientMessages) {
        AppNamespace appNamespace = appNamespaceCache.findPublicNamespaceByName(namespace);

        //check whether the appNamespace's appCode equals to current one
        if (Objects.isNull(appNamespace) || Objects.equals(clientAppId, appNamespace.getApp().getId())) {
            return null;
        }
        AppEnvClusterNamespace clusterNamespace = appNamespace.getNamespaceByEnv(env, clusterName, "main");

        return clusterNamespace.findLatestActiveRelease();
    }


    /**
     * 生成发布配置：继承关系，需要多个Map进行合并
     */
    Map<String, String> mergeReleaseConfigurations(List<Release> releases) {
        Map<String, String> result = Maps.newHashMap();
        for (Release release : Lists.reverse(releases)) {
            result.putAll(gson.fromJson(release.getConfigurations(), configurationTypeReference));
        }
        return result;
    }

    private String assembleKey(String appId, String cluster, String namespace, String dataCenter) {
        List<String> keyParts = Lists.newArrayList(appId, cluster, namespace);
        if (!isNullOrEmpty(dataCenter)) {
            keyParts.add(dataCenter);
        }
        return keyParts.stream().collect(Collectors.joining(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR));
    }

    private void heartBeatReleases(NamespaceBo namespaceBo, String clientIp,
                                   List<Release> releases) {
        if (isNullOrEmpty(clientIp)) {
            //no need to offerHeartBeat instance config when there is no ip
            return;
        }
        for (Release release : releases) {
            heartBeatPool.offerHeartBeat(namespaceBo, clientIp, release.getReleaseKey());
        }
    }


}

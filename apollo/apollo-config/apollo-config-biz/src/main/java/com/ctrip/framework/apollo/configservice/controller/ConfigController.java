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

import com.ctrip.framework.apollo.configservice.pattern.pool.HeartBeatPool;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yofish.apollo.domain.Release;
import common.NamespaceBo;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.dto.ApolloConfig;
import framework.apollo.tracer.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ctrip.framework.apollo.configservice.util.IpUtils.tryToGetClientIp;
import static com.google.common.base.Strings.isNullOrEmpty;

@RestController
@RequestMapping("/configs")
public class ConfigController {
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


        ConfigClient configClient = new ConfigClient(appId, clusterName, env, namespace, dataCenter, clientIp, messagesAsString);

        List<Release> releases = configClient.findReleases4Client();

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


    private String filterAndNormalizeNamespace(@PathVariable String appId, @PathVariable String namespace) {
        //strip out .properties suffix
        namespace = namespaceUtil.subSuffix4Properties(namespace);
        //fix the character case issue, such as FX.apollo <-> fx.apollo
        namespace = namespaceUtil.fixCapsLook4NamespaceName(appId, namespace);
        return namespace;
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

    /**
     * 发送心跳，刷新心跳实例
     * @param namespaceBo
     * @param clientIp
     * @param releases
     */
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

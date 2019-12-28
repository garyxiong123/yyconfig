package com.ctrip.framework.apollo.configservice.controller;

import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.ctrip.framework.apollo.configservice.service.config.ConfigService;
import com.ctrip.framework.apollo.configservice.util.InstanceConfigAuditUtil;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Release;
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

import static com.google.common.base.Strings.isNullOrEmpty;
import static framework.apollo.core.ConfigConsts.NO_APPID_PLACEHOLDER;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RestController
@RequestMapping("/configs")
public class QueryConfigController {
    private static final Splitter X_FORWARDED_FOR_SPLITTER = Splitter.on(",").omitEmptyStrings()
            .trimResults();
    @Autowired
    private ConfigService configService;
    @Autowired
    private AppNamespaceServiceWithCache appNamespaceService;
    @Autowired
    private NamespaceUtil namespaceUtil;
    @Autowired
    private InstanceConfigAuditUtil instanceConfigAuditUtil;
    @Autowired
    private Gson gson;

    private static final Type configurationTypeReference = new TypeToken<Map<String, String>>() {
    }.getType();

    @RequestMapping(value = "/{appId}/{clusterName}/{env}/{namespace:.+}", method = RequestMethod.GET)
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

        ApolloNotificationMessages clientMessages = transformMessages(messagesAsString);

        List<Release> releases = Lists.newLinkedList();

        String appClusterNameLoaded = clusterName;
        if (!NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
            Release currentRelease4ThisClient = configService.loadConfig4SingleClient(appId, clientIp, appId, clusterName, env, namespace, dataCenter, clientMessages);

            if (currentRelease4ThisClient != null) {
                releases.add(currentRelease4ThisClient);
                //we have cluster search process, so the cluster name might be overridden
                appClusterNameLoaded = currentRelease4ThisClient.getAppEnvClusterNamespace().getAppNamespace().getName();
            }
        }

        //if appNamespace does not belong to this appId, should check if there is a public configuration
        if (!namespaceBelongsToAppId(appId, namespace)) {
            Release publicRelease = this.findPublicConfig(appId, clientIp, clusterName, env, namespace,
                    dataCenter, clientMessages);
            if (!Objects.isNull(publicRelease)) {
                releases.add(publicRelease);
            }
        }

        if (releases.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    String.format(
                            "Could not load configurations with appId: %s, clusterName: %s, appNamespace: %s",
                            appId, clusterName, originalNamespace));
            return null;
        }

        auditReleases(appId, clusterName, dataCenter, clientIp, releases);

        String mergedReleaseKey = releases.stream().map(Release::getReleaseKey)
                .collect(Collectors.joining(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR));

        if (mergedReleaseKey.equals(clientSideReleaseKey)) {
            // Client side configuration is the same with server side, return 304
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            Tracer.logEvent("Apollo.Config.NotModified",
                    assembleKey(appId, appClusterNameLoaded, originalNamespace, dataCenter));
            return null;
        }

        ApolloConfig apolloConfig = new ApolloConfig(appId, appClusterNameLoaded, originalNamespace,
                mergedReleaseKey);
        apolloConfig.setConfigurations(mergeReleaseConfigurations(releases));

        return apolloConfig;
    }

    private String filterAndNormalizeNamespace(@PathVariable String appId, @PathVariable String namespace) {
        //strip out .properties suffix
        namespace = namespaceUtil.filterNamespaceName(namespace);
        //fix the character case issue, such as FX.apollo <-> fx.apollo
        namespace = namespaceUtil.normalizeNamespace(appId, namespace);
        return namespace;
    }


    private boolean namespaceBelongsToAppId(String appId, String namespaceName) {
        //Every app has an 'application' appNamespace
        if (Objects.equals(ConfigConsts.NAMESPACE_APPLICATION, namespaceName)) {
            return true;
        }

        //if no appId is present, then no other appNamespace belongs to it
        if (NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
            return false;
        }

        AppNamespace appNamespace = appNamespaceService.findByAppIdAndNamespace(appId, namespaceName);

        return appNamespace != null;
    }

    private Release findPublicConfig(String clientAppId, String clientIp, String clusterName,String env,
                                     String namespace, String dataCenter, ApolloNotificationMessages clientMessages) {
        AppNamespace appNamespace = appNamespaceService.findPublicNamespaceByName(namespace);

        //check whether the appNamespace's appId equals to current one
        if (Objects.isNull(appNamespace) || Objects.equals(clientAppId, appNamespace.getApp().getId())) {
            return null;
        }

        String publicConfigAppId = String.valueOf(appNamespace.getApp().getId());

        return configService.loadConfig4SingleClient(clientAppId, clientIp, publicConfigAppId, clusterName, env,namespace, dataCenter, clientMessages);
    }


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

    private void auditReleases(String appId, String cluster, String dataCenter, String clientIp,
                               List<Release> releases) {
        if (isNullOrEmpty(clientIp)) {
            //no need to audit instance config when there is no ip
            return;
        }
        for (Release release : releases) {
            instanceConfigAuditUtil.audit(appId, cluster, dataCenter, clientIp, release.getAppEnvClusterNamespace().getAppEnvCluster().getApp().getAppCode(),
                    release.getAppEnvClusterNamespace().getAppEnvCluster().getName(),
                    release.getAppEnvClusterNamespace().getAppNamespace().getName(), release.getReleaseKey());
        }
    }

    private String tryToGetClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-FORWARDED-FOR");
        if (!isNullOrEmpty(forwardedFor)) {
            return X_FORWARDED_FOR_SPLITTER.splitToList(forwardedFor).get(0);
        }
        return request.getRemoteAddr();
    }

    ApolloNotificationMessages transformMessages(String messagesAsString) {
        ApolloNotificationMessages notificationMessages = null;
        if (!isNullOrEmpty(messagesAsString)) {
            try {
                notificationMessages = gson.fromJson(messagesAsString, ApolloNotificationMessages.class);
            } catch (Throwable ex) {
                Tracer.logError(ex);
            }
        }

        return notificationMessages;
    }
}

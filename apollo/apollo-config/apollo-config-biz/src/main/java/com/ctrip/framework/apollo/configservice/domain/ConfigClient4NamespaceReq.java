package com.ctrip.framework.apollo.configservice.domain;

import com.ctrip.framework.apollo.configservice.controller.timer.AppNamespaceCache;
import com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease.ClientLoadReleaseStrategy;
import com.google.common.collect.Lists;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Release;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;

import java.util.List;
import java.util.Objects;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass4Context;
import static com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts.CLUSTER_NAME_DEFAULT;
import static com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts.NO_APPID_PLACEHOLDER;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/8/22 下午11:04
 */
public class ConfigClient4NamespaceReq extends ConfigClient {

    public String namespace;
    public LongNamespaceVersion clientMessages;

    public ConfigClient4NamespaceReq() {
    }

    public ConfigClient4NamespaceReq(String appId, String clusterName, String env, String namespace, String dataCenter, String clientIp, String longNsVersionMapString) {
        super(appId, clusterName, env, dataCenter, clientIp);
        this.namespace = namespace;
        this.clientMessages = LongNamespaceVersion.buildLongNamespaceVersion(longNsVersionMapString);
    }

    /**
     * 查找该client 的所有的releases
     */
    public List<Release> findReleases4Client() {
        List<Release> releases = Lists.newLinkedList();

        if (!NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
            Release release4Client = getBeanByClass4Context(ClientLoadReleaseStrategy.class).loadRelease4Client(appId, clientIp, appId, clusterName, env, namespace, dataCenter, clientMessages);
            if (release4Client != null) {
                releases.add(release4Client);
            }
        }

        //if appNamespace does not belong to this appCode, should check if there is a public configuration
        if (isPublicNamespace(appId, namespace)) {
            Release publicRelease = findPublicConfig(appId, clientIp, clusterName, env, namespace, dataCenter, clientMessages);
            if (!Objects.isNull(publicRelease)) {
                releases.add(publicRelease);
            }
        }
        return releases;
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

        AppNamespace appNamespace = getBeanByClass4Context(AppNamespaceCache.class).findByAppIdAndNamespace(appId, namespaceName);

        return appNamespace == null;
    }

    private Release findPublicConfig(String clientAppId, String clientIp, String clusterName, String env,
                                     String namespace, String dataCenter, LongNamespaceVersion clientMessages) {
        AppNamespace appNamespace = getBeanByClass4Context(AppNamespaceCache.class).findPublicNamespaceByName(namespace);

        //check whether the appNamespace's appCode equals to current one
        if (Objects.isNull(appNamespace) || Objects.equals(clientAppId, appNamespace.getApp().getId())) {
            return null;
        }
        AppEnvClusterNamespace clusterNamespace = appNamespace.getNamespaceByEnv(env, CLUSTER_NAME_DEFAULT, "main");

        return clusterNamespace.findLatestActiveRelease();
    }

}

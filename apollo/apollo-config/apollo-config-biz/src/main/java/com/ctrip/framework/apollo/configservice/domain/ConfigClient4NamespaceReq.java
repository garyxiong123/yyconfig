package com.ctrip.framework.apollo.configservice.domain;

import com.ctrip.framework.apollo.configservice.cache.ReleaseCache;
import com.ctrip.framework.apollo.configservice.component.ConfigClient;
import com.ctrip.framework.apollo.configservice.cache.AppNamespaceCache;
import com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease.ClientLoadReleaseStrategy;
import com.ctrip.framework.apollo.configservice.component.ReleaseRepo;
import com.google.common.collect.Lists;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.pattern.listener.releasemessage.GrayReleaseRulesHolder;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;

import java.util.List;
import java.util.Objects;

import static com.ctrip.framework.apollo.configservice.cache.ReleaseCache.STRING_SPLITTER;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;
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
    public String configAppId;
    public String configClusterName;
    public String configNamespace;//the requested config's appNamespace name

    public ConfigClient4NamespaceReq() {
    }

    public ConfigClient4NamespaceReq(String appId, String clusterName, String env, String namespace, String dataCenter, String clientIp, String longNsVersionMapString) {
        super(appId, clusterName, env, dataCenter, clientIp);
        this.namespace = namespace;
        this.clientMessages = LongNamespaceVersion.buildLongNamespaceVersion(longNsVersionMapString);
        parseClientMsg(clientMessages);//??
    }

    private void parseClientMsg(LongNamespaceVersion clientMessages) {
        String longNsNameString = clientMessages.getLongNsVersionMap().keySet().iterator().next();
        List<String> longNsSet = STRING_SPLITTER.splitToList(longNsNameString);
        configAppId = longNsSet.get(0);
        configClusterName = longNsSet.get(1);
        configNamespace = longNsSet.get(3);
    }

    /**
     * 查找该client 的所有的releases
     */
    public List<Release> findReleases4Client() {
        List<Release> releases = Lists.newLinkedList();

        if (isAppIdValid()) {
            Release release4Client = getBeanByClass4Context(ClientLoadReleaseStrategy.class).loadRelease4Client(this);
            if (release4Client != null) {
                releases.add(release4Client);
            }
        }

        //if appNamespace does not belong to this appCode, should check if there is a public configuration
        if (isPublicNamespace()) {
            Release publicRelease = findPublicConfig();
            if (!Objects.isNull(publicRelease)) {
                releases.add(publicRelease);
            }
        }
        return releases;
    }

    private boolean isAppIdValid() {
        return !NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId);
    }


    /**
     * 该AppId 的命名空间中没有 appNamespace
     */
    public boolean isPublicNamespace() {
        //Every app has an 'application' appNamespace
        if (Objects.equals(ConfigConsts.NAMESPACE_APPLICATION, namespace)) {
            return false;
        }

        //if no appCode is present, then no other appNamespace belongs to it
        if (NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
            return true;
        }

        AppNamespace appNamespace = getBeanByClass4Context(AppNamespaceCache.class).findByAppIdAndNamespace(appId, namespace);

        return appNamespace == null;
    }

    /**
     * 非本项目的 公共命名空间的读取 =》 先读统一集群，再读默认
     */
    public Release findPublicConfig() {
        AppNamespace appNamespace = getBeanByClass4Context(AppNamespaceCache.class).findPublicNamespaceByName(namespace);

        //check whether the appNamespace's appCode equals to current one
        if (Objects.isNull(appNamespace) || Objects.equals(appId, appNamespace.getApp().getId())) {
            return null;
        }
        AppEnvClusterNamespace clusterNamespace = appNamespace.getNamespaceByEnv(env, clusterName, "main");
        if (clusterNamespace == null) {
            clusterNamespace = appNamespace.getNamespaceByEnv(env, CLUSTER_NAME_DEFAULT, "main");
        }

        return clusterNamespace.findLatestActiveRelease();
    }


    /**
     * Find release
     *
     * @param clientAppId       the client's app id
     * @param clientIp          the client ip
     * @param configAppId       the requested config's app id
     * @param configClusterName the requested config's cluster name
     * @param configNamespace   the requested config's appNamespace name
     * @param clientMessages    the messages received in client side
     * @return the release
     */
    public Release findRelease(String clientAppId, String clientIp, String configAppId, String env, String configClusterName,
                               String configNamespace, LongNamespaceVersion clientMessages) {

        Long grayReleaseId = getBeanByClass(GrayReleaseRulesHolder.class).findReleaseIdFromGrayReleaseRule(clientAppId, clientIp, configAppId,
                configClusterName, configNamespace);

        Release release = null;

        if (grayReleaseId != null) {
            release = getBeanByClass(ReleaseCache.class).findActiveOne(grayReleaseId, clientMessages);
        }

        if (release == null) {
            release = getBeanByClass(ReleaseCache.class).findLatestActiveRelease(configAppId, env, configClusterName, configNamespace, clientMessages);
        }

        return release;
    }


    public Release loadReleaseViaDefaultCluster() {
        return findRelease(appId, clientIp, configAppId, env, ConfigConsts.CLUSTER_NAME_DEFAULT, configNamespace, clientMessages);
    }


    public Release tryToLoadViaDataCenter() {
        return findRelease(appId, clientIp, configAppId, dataCenter, configNamespace, env, clientMessages);
    }


    public Release tryToLoadViaSpecifiedCluster() {
        return findRelease(appId, clientIp, configAppId, env, configClusterName, configNamespace, clientMessages);
    }


    public boolean isDataCenterValid() {
        return !isNullOrEmpty(dataCenter) && !Objects.equals(dataCenter, configClusterName);
    }

}

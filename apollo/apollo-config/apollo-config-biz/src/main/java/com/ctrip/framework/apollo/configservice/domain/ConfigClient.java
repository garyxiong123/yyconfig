package com.ctrip.framework.apollo.configservice.domain;

import com.ctrip.framework.apollo.configservice.controller.timer.AppNamespaceCache;
import com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease.ClientLoadReleaseStrategy;
import com.google.common.collect.Lists;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Release;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass4Context;
import static com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts.NO_APPID_PLACEHOLDER;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: config 端的 Client 配置抽象
 * @Date: 2020/8/18 下午4:04
 */
@Data
@NoArgsConstructor
public class ConfigClient {
    protected String appId;
    protected String clusterName;
    protected String env;
    protected String dataCenter;
    protected String clientIp;


    public ConfigClient(String appId, String clusterName, String env, String dataCenter, String clientIp) {
        this.appId = appId;
        this.clusterName = clusterName;
        this.env = env;

        this.dataCenter = dataCenter;
        this.clientIp = clientIp;

    }




}

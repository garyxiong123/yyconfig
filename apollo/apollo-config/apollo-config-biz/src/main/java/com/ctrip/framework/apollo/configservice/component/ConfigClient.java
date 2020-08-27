package com.ctrip.framework.apollo.configservice.component;

import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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


    public boolean isDefaultCluster() {
        return Objects.equals(ConfigConsts.CLUSTER_NAME_DEFAULT, clusterName);
    }



}

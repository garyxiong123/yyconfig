package com.ctrip.framework.apollo.configservice;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import common.NamespaceBo;
import framework.apollo.core.ConfigConsts;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/30 下午3:25
 */
@Data
public class InstanceConfigRefreshModel {
    private String ip;
    private String releaseKey;
    private LocalDateTime offerTime;
    private NamespaceBo namespaceBo;


    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);


    public InstanceConfigRefreshModel(NamespaceBo namespaceBo, String clientIp, String  releaseKey) {
        this.namespaceBo = namespaceBo;
        this.offerTime = LocalDateTime.now();
        this.ip = clientIp;
        this.releaseKey = releaseKey;
    }


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        InstanceConfigAuditUtil.InstanceConfigAuditModel model = (InstanceConfigAuditUtil.InstanceConfigAuditModel) o;
//        return Objects.equals(appCode, model.appCode) &&
//                Objects.equals(clusterName, model.clusterName) &&
//                Objects.equals(dataCenter, model.dataCenter) &&
//                Objects.equals(ip, model.ip) &&
//                Objects.equals(configAppCode, model.configAppCode) &&
//                Objects.equals(configClusterName, model.configClusterName) &&
//                Objects.equals(namespaceName, model.namespaceName) &&
//                Objects.equals(releaseKey, model.releaseKey);
//    }

    public String assembleInstanceConfigKey(Long instanceId) {

        return STRING_JOINER.join(instanceId, namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getClusterName(),  namespaceBo.getNamespaceName());


    }

    public String assembleInstanceKey() {
        List<String> keyParts = Lists.newArrayList(namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getClusterName(), ip);
        if (!Strings.isNullOrEmpty(namespaceBo.getDataCenter())) {
            keyParts.add(namespaceBo.getDataCenter());
        }
        return STRING_JOINER.join(keyParts);
    }
}
package com.ctrip.framework.apollo.configservice.controller;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import framework.apollo.core.ConfigConsts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/13 下午2:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public  class ConfigReqDto {

    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);

    private ConfigFileController.ConfigFileOutputFormat configFileOutputFormat;
    private String appId;
    private String clusterName;
    private String namespace;
    private String dataCenter;
    private String clientIp;
    private String env;

    public String assembleCacheKey() {

        List<String> keyParts = Lists.newArrayList(configFileOutputFormat.getValue(), appId, clusterName, namespace);

        if (!isNullOrEmpty(dataCenter)) {
            keyParts.add(dataCenter);
        }
        return STRING_JOINER.join(keyParts);
    }

     protected String getConfigResult(Map<String, String> configurations){
       return null;
     };
}

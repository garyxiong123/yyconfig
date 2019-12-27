package com.ctrip.framework.apollo.configservice.controller;

import com.google.gson.Gson;

import java.util.Map;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/13 下午2:30
 */
public class ConfigReqDto4Json extends ConfigReqDto {

    private static final Gson gson = new Gson();

    public ConfigReqDto4Json(ConfigFileController.ConfigFileOutputFormat configFileOutputFormat, String appId, String clusterName,String env, String namespace, String dataCenter, String clientIp) {
        super(configFileOutputFormat, appId, clusterName, env, namespace, dataCenter, clientIp);
    }

    public ConfigReqDto4Json() {
        this.setConfigFileOutputFormat(ConfigFileController.ConfigFileOutputFormat.JSON);
    }


    @Override
    public String getConfigResult(Map<String, String> configurations) {
        String result = gson.toJson(configurations);

        return result;
    }
}

package com.ctrip.framework.apollo.configservice.controller;

import framework.apollo.core.utils.PropertiesUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/13 下午2:30
 */
public class ConfigReqDto4Properties extends ConfigReqDto {

    public ConfigReqDto4Properties(ConfigFileController.ConfigFileOutputFormat configFileOutputFormat, String appId, String clusterName, String env,String namespace, String dataCenter, String clientIp) {
        super(configFileOutputFormat, appId, clusterName, env,namespace, dataCenter, clientIp);
    }

    public ConfigReqDto4Properties() {
        this.setConfigFileOutputFormat(ConfigFileController.ConfigFileOutputFormat.PROPERTIES);
    }

    @Override
    public String getConfigResult(Map<String, String> configurations) {

        Properties properties = new Properties();
        properties.putAll(configurations);
        String result = null;
        try {
            result = PropertiesUtil.toString(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

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
package com.ctrip.framework.apollo.configservice.api.dto;

import com.ctrip.framework.apollo.configservice.api.enums.ConfigFileOutputFormat;
import com.yofish.yyconfig.common.framework.apollo.core.utils.PropertiesUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/13 下午2:30
 */
public class ConfigReqDto4Properties extends ConfigReqDto {

    public ConfigReqDto4Properties(ConfigFileOutputFormat configFileOutputFormat, String appId, String clusterName, String env,String namespace, String dataCenter, String clientIp) {
        super(configFileOutputFormat, appId, clusterName, env,namespace, dataCenter, clientIp);
    }

    public ConfigReqDto4Properties() {
        this.setConfigFileOutputFormat(ConfigFileOutputFormat.PROPERTIES);
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

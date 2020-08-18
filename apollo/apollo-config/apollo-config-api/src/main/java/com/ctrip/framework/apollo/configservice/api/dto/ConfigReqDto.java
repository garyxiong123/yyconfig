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

    private ConfigFileOutputFormat configFileOutputFormat;
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

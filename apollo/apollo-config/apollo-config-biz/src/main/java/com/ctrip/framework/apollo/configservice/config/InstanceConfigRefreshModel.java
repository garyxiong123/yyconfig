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
package com.ctrip.framework.apollo.configservice.config;

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


    public InstanceConfigRefreshModel(NamespaceBo namespaceBo, String clientIp, String releaseKey) {
        this.namespaceBo = namespaceBo;
        this.offerTime = LocalDateTime.now();
        this.ip = clientIp;
        this.releaseKey = releaseKey;
    }

    public String assembleInstanceConfigKey(Long instanceId) {

        return STRING_JOINER.join(instanceId, namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getClusterName(), namespaceBo.getNamespaceName());


    }

    public String assembleInstanceKey() {
        List<String> keyParts = Lists.newArrayList(namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getClusterName(), ip);
        if (!Strings.isNullOrEmpty(namespaceBo.getDataCenter())) {
            keyParts.add(namespaceBo.getDataCenter());
        }
        return STRING_JOINER.join(keyParts);
    }
}
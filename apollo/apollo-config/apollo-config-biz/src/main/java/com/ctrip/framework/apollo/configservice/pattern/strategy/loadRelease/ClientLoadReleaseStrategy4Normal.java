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
package com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease;

import com.ctrip.framework.apollo.configservice.component.util.LongNamespaceNameUtil;
import com.ctrip.framework.apollo.configservice.component.util.NamespaceNormalizer;
import com.ctrip.framework.apollo.configservice.domain.ConfigClient4NamespaceReq;
import com.ctrip.framework.apollo.configservice.component.ReleaseRepo;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.pattern.listener.releasemessage.GrayReleaseRulesHolder;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass4Context;
import static com.yofish.yyconfig.common.common.utils.YyStringUtils.notEqual;
import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * 客户端加载配置 策略
 */
@Component
public class ClientLoadReleaseStrategy4Normal implements ClientLoadReleaseStrategy {
    @Autowired
    private GrayReleaseRulesHolder grayReleaseRulesHolder;
    @Qualifier("ReleaseCache")
    private ReleaseRepo releaseRepo;







    @Override
    public Release loadRelease4Client(ConfigClient4NamespaceReq configClient4NamespaceReq) {
        // 特殊集群： 非默认，
        if (!configClient4NamespaceReq.isDefaultCluster()) {
            Release clusterRelease = configClient4NamespaceReq.tryToLoadViaSpecifiedCluster();
            if (!isNull(clusterRelease)) {
                return clusterRelease;
            }
        }
        // 特殊的数据中心： 非默认
        if (configClient4NamespaceReq.isDataCenterValid()) {
            Release dataCenterRelease = configClient4NamespaceReq.tryToLoadViaDataCenter();
            if (!isNull(dataCenterRelease)) {
                return dataCenterRelease;
            }
        }

        // fallback to default release
        return configClient4NamespaceReq.loadReleaseViaDefaultCluster();
    }





}

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
package com.yofish.apollo.pattern.strategy.publish;

import com.google.common.collect.Maps;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.pattern.algorithm.CalculateUtil;
import com.yofish.apollo.repository.ReleaseHistoryRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.gary.annotation.StrategyNum;
import com.yofish.yyconfig.common.common.constants.ReleaseOperationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.yofish.yyconfig.common.common.constants.ReleaseOperation.NORMAL_RELEASE;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/18 下午6:06
 */
@StrategyNum(superClass = PublishStrategy.class, number = "publishStrategy4Main", describe = "主提交发布策略")
@Component
public class PublishStrategy4Main extends PublishStrategy {

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private ReleaseHistoryRepository releaseHistoryRepository;


    @Override
    public Release publish(Release release4Main) {

        masterRelease(release4Main, NORMAL_RELEASE);
        AppEnvClusterNamespace4Main namespace4Main = (AppEnvClusterNamespace4Main) release4Main.getAppEnvClusterNamespace();

        if (namespace4Main.hasBranchNamespace()) {

            Release4Branch release4Branch =  createBranchRelease(release4Main, namespace4Main.getBranchNamespace());
            release4Branch.publish();
//            branchRelease(release4Branch, NORMAL_RELEASE);
        }

        return release4Main;
    }

    private Release4Branch createBranchRelease(Release release4Main, AppEnvClusterNamespace4Branch branchNamespace) {

        return Release4Branch.builder().name(release4Main.getName()).namespace(branchNamespace).comment(release4Main.getComment()).build();
    }

    private Release masterRelease(Release release4Main, int releaseOperation) {

        Map<String, Object> operationContext = Maps.newHashMap();
        operationContext.put(ReleaseOperationContext.IS_EMERGENCY_PUBLISH, release4Main.isEmergencyPublish());

        Map<String, String> configurations = CalculateUtil.calculateConfigs(release4Main);
        release4Main.setConfigurations(gson.toJson(configurations));

        createReleaseAndUnlock(release4Main);

        createReleaseHistory(release4Main,operationContext, releaseOperation);

        return release4Main;
    }
}

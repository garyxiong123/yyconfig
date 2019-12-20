package com.yofish.apollo.strategy;

import com.google.common.collect.Maps;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.Release4Main;
import com.yofish.apollo.repository.ReleaseHistoryRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.gary.annotation.StrategyNum;
import common.constants.ReleaseOperation;
import common.constants.ReleaseOperationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.yofish.apollo.strategy.CalculateUtil.calculateConfigs;
import static common.constants.ReleaseOperation.*;

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

        if (((Release4Main) release4Main).getBranchRelease() != null) {

            branchRelease(((Release4Main) release4Main).getBranchRelease(), NORMAL_RELEASE);
        }

        return release4Main;
    }

    private Release masterRelease(Release release4Main, int releaseOperation) {

        Map<String, Object> operationContext = Maps.newHashMap();
        operationContext.put(ReleaseOperationContext.IS_EMERGENCY_PUBLISH, release4Main.isEmergencyPublish());

        Map<String, String> configurations = calculateConfigs(release4Main);
        release4Main.setConfigurations(gson.toJson(configurations));

        createReleaseAndUnlock(release4Main);

        createReleaseHistory(release4Main);

        return release4Main;
    }
}

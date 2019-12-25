package com.yofish.apollo.strategy;

import com.google.common.collect.Maps;
import com.yofish.apollo.domain.GrayReleaseRule;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.Release4Branch;
import com.yofish.gary.annotation.StrategyNum;
import common.constants.ReleaseOperation;
import common.constants.ReleaseOperationContext;
import common.utils.GrayReleaseRuleItemTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.yofish.apollo.strategy.CalculateUtil.Json2Map;
import static com.yofish.apollo.strategy.CalculateUtil.calculateBranchNamespaceConfigsToPublish;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/18 下午6:06
 */

@StrategyNum(superClass = PublishStrategy.class, number = "publishStrategy4Branch", describe = "分支提交发布策略")
@Component
public class PublishStrategy4Branch extends PublishStrategy {


    @Override
    public Release publish(Release release4Branch) {


        Map<String, String> configToPublish = calculateBranchNamespaceConfigsToPublish(release4Branch);
        release4Branch.setConfigurations(gson.toJson(configToPublish));
        if (isConfigChanged(release4Branch, configToPublish)) {

            branchRelease((Release4Branch) release4Branch, ReleaseOperation.GRAY_RELEASE);
        }


        return release4Branch;
    }

    protected void branchRelease(Release4Branch release4Branch, int releaseOperation) {

        Map<String, Object> releaseOperationContext = Maps.newHashMap();
        if (release4Branch.getMainRelease() != null) {
            releaseOperationContext.put(ReleaseOperationContext.BASE_RELEASE_ID, release4Branch.getMainRelease().getId());
        }
        releaseOperationContext.put(ReleaseOperationContext.IS_EMERGENCY_PUBLISH, release4Branch.isEmergencyPublish());


        //update gray release rules  TODO ?? 为什么 更新灰度规则
        GrayReleaseRule grayReleaseRule = namespaceBranchService.updateRulesReleaseId(release4Branch);

        if (grayReleaseRule != null) {
            releaseOperationContext.put(ReleaseOperationContext.RULES, GrayReleaseRuleItemTransformer.batchTransformFromJSON(grayReleaseRule.getRules()));
        }
        release4Branch.setReleaseKey(release4Branch.getReleaseKey() + "branch");

        createReleaseAndUnlock(release4Branch);

        createReleaseHistory(release4Branch, releaseOperationContext, releaseOperation);

    }


    private boolean isConfigChanged(Release release4Branch, Map<String, String> configToPublish) {
        Map<String, String> currentConfig = Json2Map(release4Branch.getConfigurations());
        return !configToPublish.equals(currentConfig);
    }


}

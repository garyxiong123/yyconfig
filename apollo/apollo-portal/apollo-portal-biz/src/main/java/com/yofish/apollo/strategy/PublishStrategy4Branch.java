package com.yofish.apollo.strategy;

import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.Release4Branch;
import common.constants.ReleaseOperation;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.yofish.apollo.strategy.CalculateUtil.Json2Map;
import static com.yofish.apollo.strategy.CalculateUtil.calculateBranchNamespaceConfigsToPublish;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/18 下午6:06
 */
@Service
public class PublishStrategy4Branch extends PublishStrategy {


    @Override
    public Release publish(Release release4Branch) {


        Map<String, String> configToPublish = calculateBranchNamespaceConfigsToPublish(release4Branch);

        if (isConfigChanged(release4Branch, configToPublish)) {

            branchRelease((Release4Branch) release4Branch, ReleaseOperation.GRAY_RELEASE);
        }


        return release4Branch;
    }

    private boolean isConfigChanged(Release release4Branch, Map<String, String> configToPublish) {
        Map<String, String> currentConfig = Json2Map(release4Branch.getConfigurations());
        return !configToPublish.equals(currentConfig);
    }




}

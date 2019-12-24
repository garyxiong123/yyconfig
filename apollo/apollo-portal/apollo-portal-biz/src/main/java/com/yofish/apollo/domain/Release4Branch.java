package com.yofish.apollo.domain;

import com.yofish.apollo.strategy.PublishStrategy4Branch;
import com.yofish.apollo.strategy.PublishStrategy4Main;
import com.yofish.apollo.util.ReleaseKeyGenerator;
import common.constants.GsonType;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.util.CollectionUtils;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.*;

import static com.yofish.apollo.strategy.CalculateUtil.mergeConfiguration;
import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 上午10:54
 */
@Data
@Entity
@DiscriminatorValue("Release4Branch")
public class Release4Branch extends Release {

    @Builder
    public Release4Branch(AppEnvClusterNamespace namespace, String name, String comment, Map<String, String> configurations, boolean isEmergencyPublish) {
        super(namespace, name, comment, configurations, isEmergencyPublish);
        this.setReleaseKey(ReleaseKeyGenerator.generateReleaseKey(this.getAppEnvClusterNamespace()));

    }

    public Release4Branch() {

    }


    @Override
    public Release publish() {

        return getBeanByClass(PublishStrategy4Branch.class).publish(this);
    }


    public Release4Main getMainRelease() {
       return (Release4Main)((AppEnvClusterNamespace4Branch)this.getAppEnvClusterNamespace()).getMainNamespace().findLatestActiveRelease();
    }

    public void rollback(Release4Main release4Main, List<Release> twoLatestActiveReleases) {



        FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");
        this.setName( TIMESTAMP_FORMAT.format(new Date()) + "-master-rollback-merge-to-gray");

        this.publish();

        //        branchRelease(parentNamespace, childNamespace, TIMESTAMP_FORMAT.format(new Date()) + "-master-rollback-merge-to-gray", "",
//                childNamespaceNewConfiguration, parentNamespaceNewLatestRelease.getId(), operator,
//                ReleaseOperation.MATER_ROLLBACK_MERGE_TO_GRAY, false);

    }



    private Map<String, String> calculateBranchConfigToPublish(Map<String, String> mainNamespaceOldConfiguration, Map<String, String> mainNamespaceNewConfiguration,
                                                               AppEnvClusterNamespace branchNamespace) {
        //first. calculate child appNamespace modified configs
        Release childNamespaceLatestActiveRelease = branchNamespace.findLatestActiveRelease();

        Map<String, String> childNamespaceLatestActiveConfiguration = childNamespaceLatestActiveRelease == null ? null : gson.fromJson(childNamespaceLatestActiveRelease.getConfigurations(), GsonType.CONFIG);

        Map<String, String> childNamespaceModifiedConfiguration = calculateBranchModifiedItemsAccordingToRelease(mainNamespaceOldConfiguration, childNamespaceLatestActiveConfiguration);

        //second. append child appNamespace modified configs to parent appNamespace new latest configuration
        return mergeConfiguration(mainNamespaceNewConfiguration, childNamespaceModifiedConfiguration);
    }

    private Map<String, String> calculateBranchModifiedItemsAccordingToRelease(
            Map<String, String> masterReleaseConfigs,
            Map<String, String> branchReleaseConfigs) {

        Map<String, String> modifiedConfigs = new HashMap<>();

        if (CollectionUtils.isEmpty(branchReleaseConfigs)) {
            return modifiedConfigs;
        }

        if (CollectionUtils.isEmpty(masterReleaseConfigs)) {
            return branchReleaseConfigs;
        }

        for (Map.Entry<String, String> entry : branchReleaseConfigs.entrySet()) {

            if (!Objects.equals(entry.getValue(), masterReleaseConfigs.get(entry.getKey()))) {
                modifiedConfigs.put(entry.getKey(), entry.getValue());
            }
        }

        return modifiedConfigs;

    }


}

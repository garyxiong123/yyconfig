package com.yofish.apollo.domain;

import com.google.common.collect.Maps;
import common.constants.ReleaseOperation;
import common.constants.ReleaseOperationContext;

import javax.persistence.DiscriminatorValue;
import java.util.Map;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/15 下午10:14
 */
@DiscriminatorValue("AppEnvClusterNamespace4Main")
public class AppEnvClusterNamespace4Main extends AppEnvClusterNamespace {

    public AppEnvClusterNamespace4Main(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        super(appEnvCluster, appNamespace);
    }

    @Override
    public Release publish(String releaseName, String releaseComment, boolean isEmergencyPublish) {
        Release previousRelease = null;
        if (hasBranchNamespace()) {
            previousRelease = findLatestActiveRelease(namespace);
        }

        //master release
        Map<String, Object> operationContext = Maps.newHashMap();
        operationContext.put(ReleaseOperationContext.IS_EMERGENCY_PUBLISH, isEmergencyPublish);

        Release release = masterRelease(namespace, releaseName, releaseComment, operateNamespaceItems, operator, ReleaseOperation.NORMAL_RELEASE, operationContext);

        //merge to branch and auto release
        if (hasBranchNamespace()) {
            mergeFromMasterAndPublishBranchThenRelease(namespace, namespace.getNamespacesBranchNamespace(), operateNamespaceItems, releaseName, releaseComment, operator, previousRelease, release, isEmergencyPublish);
        }
        return null;
    }

}

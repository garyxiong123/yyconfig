package com.yofish.apollo.domain;

import com.google.common.collect.Maps;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Map;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 上午10:54
 */
@Data
@Entity
@DiscriminatorValue("Release4Main")
public class Release4Main extends Release {

    @Column(name = "Comment", nullable = false)
    private String comment;


    @Override
    public Release publish() {
        Map<String, String> operateNamespaceItems = null;
        AppEnvClusterNamespace4Main appEnvClusterNamespace4Main = (AppEnvClusterNamespace4Main) this.getAppEnvClusterNamespace();

        Release previousRelease = null;
        if (appEnvClusterNamespace4Main.hasBranchNamespace()) {
            previousRelease = appEnvClusterNamespace4Main.findLatestActiveRelease();
        }

        //master release
        Map<String, Object> operationContext = Maps.newHashMap();
//        operationContext.put(ReleaseOperationContext.IS_EMERGENCY_PUBLISH, isEmergencyPublish);
//
//        Release release = masterRelease(namespace, releaseName, releaseComment, operateNamespaceItems, operator, ReleaseOperation.NORMAL_RELEASE, operationContext);
//
//        //merge to branch and auto release
//        if (hasBranchNamespace()) {
//            mergeFromMasterAndPublishBranchThenRelease(namespace, namespace.getNamespacesBranchNamespace(), operateNamespaceItems, releaseName, releaseComment, operator, previousRelease, release, isEmergencyPublish);
//        }
        return null;
    }

//        this.getAppEnvClusterNamespace().publish(null, comment, null, isEmergencyPublish());
//        return this;
}


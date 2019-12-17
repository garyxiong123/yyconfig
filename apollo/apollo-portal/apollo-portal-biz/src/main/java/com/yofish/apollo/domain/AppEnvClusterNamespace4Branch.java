package com.yofish.apollo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Map;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/15 下午10:14
 */
@AllArgsConstructor
@Data
@Entity
@DiscriminatorValue("branch")
public class AppEnvClusterNamespace4Branch extends AppEnvClusterNamespace {

    private Long parentId;


    public AppEnvClusterNamespace4Branch(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        super(appEnvCluster, appNamespace);
    }

    @Override
    public Release publish(Map<String, String> operateNamespaceItems, String releaseName, String releaseComment, boolean isEmergencyPublish) {

//        return publishBranchNamespace(namespace, namespace, operateNamespaceItems, releaseName, releaseComment, operator, isEmergencyPublish);
        return null;
    }
}

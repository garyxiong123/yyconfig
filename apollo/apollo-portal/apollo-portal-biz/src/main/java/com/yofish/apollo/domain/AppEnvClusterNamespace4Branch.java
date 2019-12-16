package com.yofish.apollo.domain;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/15 下午10:14
 */
//@NoArgsConstructor
//@AllArgsConstructor
@Data
@Entity
@DiscriminatorValue("AppEnvClusterNamespace4Branch")
public class AppEnvClusterNamespace4Branch extends AppEnvClusterNamespace {

 /*   @Override
    public Release publish(String releaseName, String releaseComment, boolean isEmergencyPublish) {

        return publishBranchNamespace(namespace, namespace, operateNamespaceItems, releaseName, releaseComment, operator, isEmergencyPublish);
    }*/
}

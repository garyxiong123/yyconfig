package com.yofish.apollo.domain;

import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.AppNamespaceService;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;
import org.springframework.data.domain.PageRequest;

import javax.persistence.*;

import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;
import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 30)
public class AppEnvClusterNamespace extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    private AppEnvCluster appEnvCluster;

    @ManyToOne(cascade = CascadeType.ALL)
    private AppNamespace appNamespace;


    public Release findLatestActiveRelease(String appId, String clusterName, String namespaceName) {
        return getBeanByClass(ReleaseRepository.class).findFirstByAppIdAndClusterNameAndNamespaceNameAndIsAbandonedFalseOrderByIdDesc(appId, clusterName, namespaceName);
    }

//    public boolean isBranchNamespace() {
//        return getBeanInstance(AppNamespaceService.class).findAppEnvClusterNamespace4Branch(this) != null;
//    }




    public Release publish(Map<String, String> operateNamespaceItems, String releaseName, String releaseComment, boolean isEmergencyPublish) {

        return null;
    }

    public Release findLatestActiveRelease() {
        return null;
    }

    public List<Release> findLatestActiveReleases(PageRequest page) {
        return null;
    }
}

package com.yofish.apollo.domain;

import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.ReleaseHistoryService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.gary.dao.entity.BaseEntity;
import common.constants.ReleaseOperation;
import common.exception.BadRequestException;
import common.exception.NotFoundException;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "releases")
public class Release extends BaseEntity {


    @Column(nullable = false)
    private String releaseKey;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private AppEnvClusterNamespace appEnvClusterNamespace;

    @Column(name = "Configurations", nullable = false)
    @Lob
    private String configurations;

    @Column(name = "Comment", nullable = false)
    private String comment;

    private boolean abandoned;


    @Transactional
    public Release rollback() {
        if (this == null) {
            throw new NotFoundException("release not found");
        }
        if (isAbandoned()) {
            throw new BadRequestException("release is not active");
        }

        PageRequest page = new PageRequest(0, 2);
        List<Release> twoLatestActiveReleases = this.getAppEnvClusterNamespace().findLatestActiveReleases(page);
        if (twoLatestActiveReleases == null || twoLatestActiveReleases.size() < 2) {
//            throw new BadRequestException(String.format("Can't rollback appNamespace(appId=%s, clusterName=%s, namespaceName=%s) because there is only one active release",
//                    appId,
//                    clusterName,
//                    namespaceName));
        }

        setAbandoned(true);

        getBeanInstance(ReleaseRepository.class).save(this);

        getBeanInstance(ReleaseHistoryService.class).createReleaseHistory(this, twoLatestActiveReleases.get(1).getId(), ReleaseOperation.ROLLBACK, null);

        //publish child appNamespace if appNamespace has child 灰度回滚
        if (this.getAppEnvClusterNamespace().hasBranchNamespace()) {
            getBeanInstance(ReleaseService.class).rollbackChildNamespace(this, twoLatestActiveReleases);
        }
        return this;
    }


    public void publish(String releaseName, String releaseComment, boolean isEmergencyPublish) {
        Map<String, String> operateNamespaceItems = null;
        this.getAppEnvClusterNamespace().publish(operateNamespaceItems, releaseName, releaseComment, isEmergencyPublish);
    }

}

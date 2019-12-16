package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import common.constants.ReleaseOperation;
import common.exception.BadRequestException;
import common.exception.NotFoundException;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

//@NoArgsConstructor
//@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name="releases")
public class Release extends BaseEntity {

/*
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
    public Release rollback(long releaseId, String operator) {
        Release release = findOne(releaseId);
        if (release == null) {
            throw new NotFoundException("release not found");
        }
        if (release.isAbandoned()) {
            throw new BadRequestException("release is not active");
        }

        String appId = release.getAppId();
        String clusterName = release.getClusterName();
        String namespaceName = release.getNamespaceName();

        PageRequest page = new PageRequest(0, 2);
        List<Release> twoLatestActiveReleases = findActiveReleases(appId, clusterName, namespaceName, page);
        if (twoLatestActiveReleases == null || twoLatestActiveReleases.size() < 2) {
            throw new BadRequestException(String.format(
                    "Can't rollback appNamespace(appId=%s, clusterName=%s, namespaceName=%s) because there is only one active release",
                    appId,
                    clusterName,
                    namespaceName));
        }

        release.setAbandoned(true);

        releaseRepository.save(release);

        releaseHistoryService.createReleaseHistory(appId, clusterName,
                namespaceName, clusterName, twoLatestActiveReleases.get(1).getId(),
                release.getId(), ReleaseOperation.ROLLBACK, null, operator);

        //publish child appNamespace if appNamespace has child
        rollbackChildNamespace(appId, clusterName, namespaceName, twoLatestActiveReleases, operator);

        return release;
    }*/


}

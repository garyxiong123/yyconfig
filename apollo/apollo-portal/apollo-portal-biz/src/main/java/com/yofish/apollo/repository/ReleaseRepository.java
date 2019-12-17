package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface ReleaseRepository extends JpaRepository<Release, Long> {


    Release findByIdAndIsAbandonedFalse(long releaseId);

    List<Release> findReleaseByReleaseKeyIn(Set<String> releaseKeys);

    Release findFirstByAppIdAndClusterNameAndNamespaceNameAndIsAbandonedFalseOrderByIdDesc(String appId, String clusterName, String namespaceName);

    int batchDelete(String appId, String clusterName, String namespaceName, String operator);
}

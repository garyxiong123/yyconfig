package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.persistence.NamedNativeQuery;
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


//    Release findByIdAndIsAbandonedFalse(long releaseId);
//
    @Query(value = "select * from tb_task t where t.task_name = ?1", nativeQuery = true)
    Release findFirstByAppIdAndClusterNameAndNamespaceNameAndIsAbandonedFalseOrderByIdDesc(String appId, String clusterName, String namespaceName);

    @Query(value = "select * from tb_task t where t.task_name = ?1", nativeQuery = true)
    Release findByIdAndIsAbandonedFalse(long releaseId);

    @Query(value = "select * from tb_task t where t.task_name = ?1", nativeQuery = true)
    List<Release> findReleaseByReleaseKeyIn(Set<String> releaseKeys);

    @Query(value = "select * from tb_task t where t.task_name = ?1", nativeQuery = true)
    int batchDelete(String appId, String clusterName, String namespaceName, String operator);

//    int batchDelete(String appId, String clusterName, String namespaceName, String operator);

    Release findFirstByAppEnvClusterNamespace_IdAndAbandonedIsFalseOrderByIdDesc(Long namespaceId );

}

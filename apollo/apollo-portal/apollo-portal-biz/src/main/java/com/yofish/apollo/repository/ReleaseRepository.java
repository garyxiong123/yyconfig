package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Release;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    Release findByIdAndAbandonedFalse(long releaseId);



    @Query(value = "select * from tb_task t where t.task_name = ?1", nativeQuery = true)
    int batchDelete(String appId, String clusterName, String namespaceName, String operator);

//    int batchDelete(String appId, String clusterName, String namespaceName, String operator);

    List<Release> findReleasesByReleaseKeyIn(Set<String> releaseKeys);


    Release findFirstByAppEnvClusterNamespace_IdAndAbandonedIsFalseOrderByIdDesc(Long namespaceId );

    List<Release> findByAppEnvClusterNamespace_IdAndAbandonedIsFalseOrderByIdDesc(Long namespaceId, Pageable page );

    List<Release> findByAppEnvClusterNamespace_IdOrderByIdDesc(Long namespaceId, Pageable page );


}

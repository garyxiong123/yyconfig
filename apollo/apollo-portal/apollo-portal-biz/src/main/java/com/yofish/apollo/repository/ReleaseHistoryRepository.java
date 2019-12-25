package com.yofish.apollo.repository;

import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.ReleaseHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ReleaseHistoryRepository extends PagingAndSortingRepository<ReleaseHistory, Long> {

    Page<ReleaseHistory> findByNamespaceIdOrderByIdDesc(Long namespaceId, Pageable pageable);

    Page<ReleaseHistory> findReleaseHistoriesByRelease(Release release, Pageable pageable);

    Page<ReleaseHistory> findReleaseHistorysByReleaseAndOperationOrderByIdDesc(Release release, int operation, Pageable pageable);

    Page<ReleaseHistory> findByPreviousReleaseIdAndOperationOrderByIdDesc(long previousReleaseId, int operation, Pageable pageable);


    @Modifying
    @Query("update ReleaseHistory set isdeleted=1,DataChange_LastModifiedBy = ?4 where appCode=?1 and clusterName=?2 and namespaceName = ?3")
    int batchDelete(String appId, String clusterName, String namespaceName, String operator);
}

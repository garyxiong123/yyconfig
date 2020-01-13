/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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

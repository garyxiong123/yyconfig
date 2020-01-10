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

//    int batchDelete(String appCode, String clusterName, String namespaceName, String operator);

    List<Release> findReleasesByReleaseKeyIn(Set<String> releaseKeys);


    Release findFirstByAppEnvClusterNamespace_IdAndAbandonedIsFalseOrderByIdDesc(Long namespaceId );

    List<Release> findByAppEnvClusterNamespace_IdAndAbandonedIsFalseOrderByIdDesc(Long namespaceId, Pageable page );

    List<Release> findByAppEnvClusterNamespace_IdOrderByIdDesc(Long namespaceId, Pageable page );


}

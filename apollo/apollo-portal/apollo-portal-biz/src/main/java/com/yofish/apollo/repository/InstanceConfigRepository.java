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

import com.yofish.apollo.domain.Instance;
import com.yofish.apollo.domain.InstanceConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface InstanceConfigRepository extends PagingAndSortingRepository<InstanceConfig, Long> {

/*@Query(value = "select ins.* " +
        "from instance_config as infg inner join Instance ins on infg.instance_id = ins.id" +
        "where ins.app_namespace_id =?1" +
        "  and infg.release_key =?2",
countQuery = "select count(1)" +
        "from instance_config as infg\n" +
        "            inner join Instance ins on infg.instance_id = ins.id" +
        "where ins.app_namespace_id =?1\n" +
        "  and infg.release_key =?2")
Page<Instance> findbyNamespace(Long appEnvClusterNamespaceId,String releaseKey);*/
    Page<InstanceConfig> findByReleaseKeyAndUpdateTimeAfter(String releaseKey, LocalDateTime validDate, Pageable pageable);



    List<InstanceConfig> findAllByInstanceAndUpdateTimeAfterAndReleaseKeyNotIn(Iterable<Instance> instance, LocalDateTime dateTime,Set<String> releaseKey);

    List<InstanceConfig> findAllByInstanceInAndUpdateTimeAfterAndReleaseKeyNotIn(Iterable<Instance> instance, LocalDateTime dateTime,Set<String> releaseKey);

    InstanceConfig findByInstanceIdAndAppCodeAndNamespaceNameAndEnv(long instanceId, String appCode, String namespaceName, String env);

    Page<InstanceConfig> findByNamespaceId(Long namespaceId, Pageable pageable);

    int countByNamespaceId(Long namespaceId);



}

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
Page<InstanceConfig> findByReleaseKeyAndUpdateTimeAfter(String releaseKey, LocalDateTime
        validDate, Pageable pageable);

    List<InstanceConfig> findAllByInstanceAndUpdateTimeAfterAndReleaseKeyNotIn(Iterable<Instance> instance, LocalDateTime dateTime
            ,Set<String> releaseKey);
}

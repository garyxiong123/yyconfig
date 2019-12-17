package com.yofish.apollo.repository;

import com.yofish.apollo.domain.InstanceConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface InstanceConfigRepository extends PagingAndSortingRepository<InstanceConfig, Long> {
/*
  InstanceConfig findByInstanceIdAndConfigAppIdAndConfigNamespaceName(long instanceId, String
          configAppId, String configNamespaceName);

  Page<InstanceConfig> findByReleaseKeyAndUpdateTimeAfter(String releaseKey, Date
          validDate, Pageable pageable);

  Page<InstanceConfig> findByConfigAppIdAndConfigClusterNameAndConfigNamespaceNameAndUpdateTimeAfter(
          String appId, String clusterName, String namespaceName, Date validDate, Pageable pageable);

  List<InstanceConfig> findByConfigAppIdAndConfigClusterNameAndConfigNamespaceNameAndUpdateTimeAfterAndReleaseKeyNotIn(
          String appId, String clusterName, String namespaceName, Date validDate, Set<String> releaseKey);

  @Modifying
  @Query("delete from InstanceConfig  where ConfigAppId=?1 and ConfigClusterName=?2 and ConfigNamespaceName = ?3")
  int batchDelete(String appId, String clusterName, String namespaceName);

  @Query(
      value = "select b.Id from `InstanceConfig` a inner join `Instance` b on b.Id =" +
          " a.`InstanceId` where a.`ConfigAppId` = :configAppId and a.`ConfigClusterName` = " +
          ":clusterName and a.`ConfigNamespaceName` = :namespaceName and a.`DataChange_LastTime` " +
          "> :validDate and b.`AppId` = :instanceAppId and ?#{#pageable.pageSize} > 0",
      countQuery = "select count(1) from `InstanceConfig` a inner join `Instance` b on b.id =" +
          " a.`InstanceId` where a.`ConfigAppId` = :configAppId and a.`ConfigClusterName` = " +
          ":clusterName and a.`ConfigNamespaceName` = :namespaceName and a.`DataChange_LastTime` " +
          "> :validDate and b.`AppId` = :instanceAppId",
      nativeQuery = true)
  Page<Object[]> findInstanceIdsByNamespaceAndInstanceAppId(
          @Param("instanceAppId") String instanceAppId, @Param("configAppId") String configAppId,
          @Param("clusterName") String clusterName, @Param("namespaceName") String namespaceName,
          @Param("validDate") Date validDate, Pageable pageable);*/
}

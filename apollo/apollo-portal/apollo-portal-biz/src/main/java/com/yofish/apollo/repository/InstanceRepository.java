package com.yofish.apollo.repository;

import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface InstanceRepository extends JpaRepository<Instance, Long> {

//    Instance findByAppIdAndClusterNameAndDataCenterAndIp(String appId, String clusterName, String dataCenter, String ip);

//    Instance findByAppNamespaceAndDataCenterAndIp(AppNamespace appNamespace, String dataCenter, String ip);

    List<Instance> findAllByAppEnvClusterNamespace(AppEnvClusterNamespace appEnvClusterNamespace);

    List<Instance> findInstancesByIdIn(Iterable<Long> ids);


}

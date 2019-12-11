package com.yofish.apollo.repository;

import com.yofish.apollo.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface InstanceRepository extends JpaRepository<Instance, Long> {

    Instance findByAppIdAndClusterNameAndDataCenterAndIp(String appId, String clusterName, String dataCenter, String ip);

}

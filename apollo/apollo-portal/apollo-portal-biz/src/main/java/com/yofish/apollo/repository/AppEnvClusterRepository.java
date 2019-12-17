package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
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
public interface AppEnvClusterRepository extends JpaRepository<AppEnvCluster, Long> {

    AppEnvCluster findClusterByAppAndEnvAndName(App app, String env, String name);

    AppEnvCluster findByParentClusterId(Long parentClusterId);

    List<AppEnvCluster> findByAppAndParentClusterId(App app, Long parentClusterId);
}

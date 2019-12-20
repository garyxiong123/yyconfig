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

    AppEnvCluster findClusterByAppIdAndEnvAndName(long appId, String env, String name);

    AppEnvCluster findClusterByAppAppCodeAndEnvAndName(String appCode, String env, String name);


    List<AppEnvCluster> findByApp(App app);

    List<AppEnvCluster> findByAppIdAndEnv(long appId,String env);
    List<AppEnvCluster> findByAppAppCodeAndEnv(String appCode,String env);




}

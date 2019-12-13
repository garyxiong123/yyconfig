package com.yofish.apollo.repository;

import com.yofish.apollo.domain.AppEnvClusterNamespace;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
public interface AppEnvClusterNamespaceRepository extends JpaRepository<AppEnvClusterNamespace, Long> {


    AppEnvClusterNamespace findByAppIdAAndEnvAndClusterNameAndNamespaceName(Long appId, String env, String clusterName, String namespaceName);


}

package com.yofish.apollo.repository;

import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppNamespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
public interface AppEnvClusterNamespaceRepository extends JpaRepository<AppEnvClusterNamespace, Long> {

    AppEnvClusterNamespace findByAppEnvClusterAndAppNamespace(AppEnvCluster appEnvCluster, AppNamespace appNamespace);
    @Query(nativeQuery = true, value = "select aecn.*\n" +
            "from app_env_cluster_namespace aecn\n" +
            "       inner join app_env_cluster aec on aecn.app_env_cluster_id = aec.id\n" +
            "       inner join app_namespace an on aecn.app_namespace_id=an.id\n" +
            "       inner join app on an.app_id=app.id\n" +
            "where app.app_code=?1 and aec.env=?2 and an.name=?3 and aec.name=?4 and aecn.type=?5")
    AppEnvClusterNamespace findAppEnvClusterNamespace(String appCode,String env,String namespace,String cluster,String type);
    AppEnvClusterNamespace findAppEnvClusterNamespaceById(Long id);
}

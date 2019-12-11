package com.yofish.apollo.repository;

import com.yofish.apollo.domain.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface NamespaceRepository extends JpaRepository<Namespace, Long> {


    Namespace findByAppIdAAndEnvAndClusterNameAndNamespaceName(Long appId, String env, String clusterName, String namespaceName);
}

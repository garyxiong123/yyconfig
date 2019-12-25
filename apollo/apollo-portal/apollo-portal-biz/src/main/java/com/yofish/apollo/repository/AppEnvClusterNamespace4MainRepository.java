package com.yofish.apollo.repository;

import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Branch;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Main;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
public interface AppEnvClusterNamespace4MainRepository extends JpaRepository<AppEnvClusterNamespace4Main, Long> {


    List<AppEnvClusterNamespace> findByAppEnvClusterOrderByIdAsc(AppEnvCluster appEnvCluster);
}

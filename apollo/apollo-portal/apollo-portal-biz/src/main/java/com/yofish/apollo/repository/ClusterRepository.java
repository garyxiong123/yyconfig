package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Cluster;
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
public interface ClusterRepository extends JpaRepository<Cluster, Long> {

    Cluster findByAppAndName(App app, String name);

    Cluster findClusterByAppAndEnvAndName(App app, String env, String name);

    Cluster findByParentClusterId(Long parentClusterId);

    List<Cluster> findByAppAndParentClusterId(App app, Long parentClusterId);
}

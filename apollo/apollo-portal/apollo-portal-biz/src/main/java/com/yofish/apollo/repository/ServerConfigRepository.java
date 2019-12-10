package com.yofish.apollo.repository;

import com.yofish.apollo.domain.ServerConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Repository
public interface ServerConfigRepository extends JpaRepository<ServerConfig, Long> {


    ServerConfig findByKey(String key);
}

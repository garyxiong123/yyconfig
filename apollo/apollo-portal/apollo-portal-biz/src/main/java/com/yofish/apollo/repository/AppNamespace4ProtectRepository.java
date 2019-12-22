package com.yofish.apollo.repository;

import com.yofish.apollo.domain.AppNamespace4Protect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangSongJun
 * @date 2019-12-13
 */
@Repository
public interface AppNamespace4ProtectRepository extends JpaRepository<AppNamespace4Protect, Long> {
    AppNamespace4Protect findByAppIdAndName(long appId, String name);
}

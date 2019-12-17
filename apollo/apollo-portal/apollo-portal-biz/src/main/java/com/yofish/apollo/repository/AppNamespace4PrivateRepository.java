package com.yofish.apollo.repository;

import com.yofish.apollo.domain.AppNamespace4Private;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-12-13
 */
@Repository
public interface AppNamespace4PrivateRepository extends JpaRepository<AppNamespace4Private, Long> {
    List<AppNamespace4Private> findByName(String name);
}

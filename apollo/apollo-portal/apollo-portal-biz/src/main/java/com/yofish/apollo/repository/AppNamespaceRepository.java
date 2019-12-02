package com.yofish.apollo.repository;

import com.yofish.apollo.domain.AppNamespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Repository
public interface AppNamespaceRepository extends JpaRepository<AppNamespace, Long> {

    AppNamespace findByAppIdAndName(Long appId, String namespaceName);

    AppNamespace findByName(String namespaceName);

    List<AppNamespace> findByNameAndIsPublic(String namespaceName, boolean isPublic);

    List<AppNamespace> findByIsPublicTrue();

    List<AppNamespace> findByAppId(String appId);

}

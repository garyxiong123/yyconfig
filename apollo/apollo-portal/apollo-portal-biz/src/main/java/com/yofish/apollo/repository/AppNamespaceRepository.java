package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
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

    AppNamespace findByAppAndName(App app, String namespaceName);

    AppNamespace findByAppAppCodeAndName(String appCode, String namespaceName);

    AppNamespace findByName(String namespaceName);

//    List<AppNamespace> findByNameAndType(String namespaceName, NamespaceType type);

//    List<AppNamespace> findByType(NamespaceType type);


    List<AppNamespace> findByAppId(Long appId);

    List<AppNamespace> findFirst500ByIdGreaterThanOrderByIdAsc(long maxIdScanned);

//    AppNamespace findByNameAndIsPublicTrue(String name);
}

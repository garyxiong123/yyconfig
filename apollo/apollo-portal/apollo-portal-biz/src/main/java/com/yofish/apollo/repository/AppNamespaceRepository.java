/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.enums.AppNamespaceType;
import com.yofish.apollo.enums.NamespaceType;
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

    AppNamespace findByAppAndNameAndAppNamespaceType(App app, String namespaceName, AppNamespaceType appNamespaceType);

    AppNamespace findByAppAppCodeAndName(String appCode, String namespaceName);

    AppNamespace findByName(String namespaceName);

    AppNamespace findByNameAndAppNamespaceType(String namespaceName, AppNamespaceType appNamespaceType);

    List<AppNamespace> findAllByNameAndAppNamespaceType(String namespaceName, AppNamespaceType appNamespaceType);


    List<AppNamespace> findByAppId(Long appId);

    List<AppNamespace> findFirst500ByIdGreaterThanOrderByIdAsc(long maxIdScanned);

    AppNamespace findByApp_IdAndNameAndAppNamespaceType(Long appId, String namespaceName, NamespaceType namespaceType);


    List<AppNamespace> findAllByAppNamespaceType(AppNamespaceType namespaceType);

    List<AppNamespace> findAllByAppNamespaceTypeAndAuthorizedAppContains(AppNamespaceType namespaceType, App app);


}

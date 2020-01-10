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

import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.dto.NamespaceListResp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppEnvClusterNamespaceRepository extends JpaRepository<AppEnvClusterNamespace, Long> {

    AppEnvClusterNamespace findByAppEnvClusterAndAppNamespace(AppEnvCluster appEnvCluster, AppNamespace appNamespace);


    @Query(nativeQuery = true, value = "select aecn.*\n" +
            "from app_env_cluster_namespace aecn\n" +
            "       inner join app_env_cluster aec on aecn.app_env_cluster_id = aec.id\n" +
            "       inner join app_namespace an on aecn.app_namespace_id=an.id\n" +
            "       inner join app on aec.app_id=app.id\n" +
            "where app.app_code=?1 and aec.env=?2 and an.name=?3 and aec.name=?4 and aecn.type=?5")
    AppEnvClusterNamespace findAppEnvClusterNamespace(String appCode,String env,String namespace,String cluster,String type);



    AppEnvClusterNamespace findAppEnvClusterNamespaceById(Long id);



    List<AppEnvClusterNamespace> findByAppEnvClusterOrderByIdAsc(AppEnvCluster appEnvCluster);

    @Query(nativeQuery=true,value="select *\n"+
            "            from app_env_cluster_namespace aecn\n"+
            "                   inner join app_env_cluster aec on aecn.app_env_cluster_id = aec.id\n"+
            "                   inner join app_namespace an on aecn.app_namespace_id=an.id\n"+
            "                   inner join app on aec.app_id=app.id\n"+
            "            where app.app_code=?1  and an.name=?2 and aecn.type='main'")
    List<AppEnvClusterNamespace> findbyAppAndEnvAndNamespace(String app, String namespace);



}

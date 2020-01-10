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
import com.yofish.apollo.domain.AppEnvCluster;
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
public interface AppEnvClusterRepository extends JpaRepository<AppEnvCluster, Long> {

    AppEnvCluster findClusterByAppIdAndEnvAndName(long appId, String env, String name);

    AppEnvCluster findByApp_AppCodeAndEnvAndName(String appCode, String env, String name);


    List<AppEnvCluster> findByApp(App app);

    List<AppEnvCluster> findByAppIdAndEnv(long appId,String env);

    List<AppEnvCluster> findByAppAppCodeAndEnv(String appCode,String env);




}

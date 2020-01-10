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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface AppRepository extends JpaRepository<App, Long>, JpaSpecificationExecutor<App> {

    /**
     * findByAppCode
     *
     * @param appCode
     * @return
     */
    App findByAppCode(String appCode);



    /**
     * findByAppCodeContainingOrNameContaining
     *
     * @param appId
     * @param name
     * @param pageable
     * @return
     */
    Page<App> findByAppCodeContainingOrNameContaining(String appId, String name, Pageable pageable);

    /**
     * findAllByAppCodeContainingOrNameContaining
     *
     * @param appId
     * @param name
     * @return
     */
    List<App> findAllByAppCodeContainingOrNameContaining(String appId, String name);

}

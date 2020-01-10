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
package com.yofish.apollo.component;

import java.lang.annotation.*;

/**
 * 项目的预授权
 *
 * @author WangSongJun
 * @date 2019-12-25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppPreAuthorize {
    Authorize value() default Authorize.AppDepartment;

    /**
     * 授权类型
     */
    enum Authorize {
        /**
         * 超级管理员
         */
        SuperAdmin,
        /**
         * 项目的拥有者
         */
        AppOwner,

        /**
         * 项目的负责人（参与人）
         */
        AppAdmin,

        /**
         * 项目的同部门
         */
        AppDepartment
    }
}

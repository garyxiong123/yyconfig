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
package com.yofish.apollo.component.config;


/**
 * 服务的配置项，可动态配置
 *
 * @author WangSongJun
 * @date 2019-12-10
 */
public enum ServerConfigKey {
    /**
     * 可支持的环境列表
     */
    APOLLO_PORTAL_ENVS,

    /**
     * http接口read timeout
     */
    API_READ_TIMEOUT,

    /**
     * consumer token salt
     */
    CONSUMER_TOKEN_SALT,

    /**
     * 是否允许项目管理员创建私有namespace
     */
    ADMIN_CREATE_PRIVATE_NAMESPACE_SWITCH,

    /**
     * 只对项目成员显示配置信息的环境列表，多个env以英文逗号分隔
     */
    CONFIG_VIEW_MEMBER_ONLY_ENVS;

}

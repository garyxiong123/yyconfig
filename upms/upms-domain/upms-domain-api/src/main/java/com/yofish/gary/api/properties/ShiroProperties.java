/*
 *    Copyright 2018-2019 the original author or authors.
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
package com.yofish.gary.api.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro 属性配置
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "shiro")
@Component
public class ShiroProperties {

    /**
     * url过滤key
     */
    @NotNull
    private String urlFilterKey = "url";

    /**
     * 默认未授权跳转地址
     */
    @NotNull
    private String unauthorized = "/user/unauthorized";

    /**
     * 默认未登录跳转地址
     */
    @NotNull
    private String login = "/user/needLogin";

    /**
     * 不需要认证key(可以包含白名单)
     * 注:按逗号(,)切分,支持多个
     */
    @NotNull
    private String noneUrlKeys = "/notifications/**,/*.js,/*.css,/user/login,/user/logout,/swagger-ui.html,/webjars/**,/v2/**,/swagger-resources/**,/deploy/jenkins/**,/actuator/**";

    /**
     * 需要认证key
     * 注:按逗号(,)切分,支持多个
     */
    @NotNull
    private String authcUrlKeys = "/**";

    /**
     * 需要认证key
     * 注:按逗号(,)切分,支持多个,默认等于authcUrlKeys
     */
    @NotNull
    private String permissionUrlKeys = authcUrlKeys;

    /**
     * hash散列算法名称
     */
    @NotNull
    private String hashAlgorithmName = "sha-256";

    /**
     * 算法盐值
     */
    private String salt = "da86t98hdiadnasd";

    /**
     * 默认策略:保持一个登录(不同的浏览器登录,后者剔出前者)
     *
     * @see ShiroConcurrentSessionStrategy4KeepOne
     */
    @NotNull
    private String concurrentSessionStrategyNumber = "0";

}

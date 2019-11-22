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
package com.yofish.gary.api;


import com.yofish.gary.api.properties.ShiroProperties;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro simpleHash 策略
 */
public abstract class ShiroSimpleHashStrategy {

    /**
     * 签名算法
     * 注:默认加shiro盐值
     * @see ShiroProperties (salt properties)
     *
     * @param password 密码
     * @return
     */
    public abstract String signature(String password);
}

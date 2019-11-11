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
package com.yofish.gary.component.strategy.shiro.signature;

import com.yofish.gary.annotation.StrategyNum;
import com.yofish.gary.api.properties.ShiroProperties;
import com.yofish.gary.api.ShiroSimpleHashStrategy;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro simpleHash md5策略
 */
@Component
@StrategyNum(superClass = ShiroSimpleHashStrategy.class, number = "md5", describe = "md5算法")
public class ShiroSimpleHashStrategy4Md5 extends ShiroSimpleHashStrategy {

    @Autowired
    private ShiroProperties shiroProperties;

    @Override
    public String signature(String password) {
        SimpleHash simpleHash = new Md5Hash(password, shiroProperties.getSalt());
        return simpleHash.toHex();
    }
}

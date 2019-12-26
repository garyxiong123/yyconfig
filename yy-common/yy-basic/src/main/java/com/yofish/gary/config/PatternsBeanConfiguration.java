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
package com.yofish.gary.config;

import com.yofish.gary.bean.BridgeBean;
import com.yofish.gary.bean.StrategyNumBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 模式Bean自动装配
 */
@Configuration
@ConditionalOnClass({StrategyNumBean.class, BridgeBean.class})
public class PatternsBeanConfiguration {

    @Bean
    @ConditionalOnMissingBean(StrategyNumBean.class)
    public StrategyNumBean strategyNumBean() {
        StrategyNumBean strategyNumBean = new StrategyNumBean();
        return strategyNumBean;
    }

    @Bean
    @ConditionalOnMissingBean(BridgeBean.class)
    public BridgeBean bridgePostProcessorBean() {
        BridgeBean bridgeBean = new BridgeBean();
        return bridgeBean;
    }
}

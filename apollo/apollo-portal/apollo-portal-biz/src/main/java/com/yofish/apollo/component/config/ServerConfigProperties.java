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

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author WangSongJun
 * @date 2020-01-02
 */
@Component
@ConfigurationProperties(prefix = "apollo")
public class ServerConfigProperties {
    @Setter
    private Map<SystemInitConfigKey, String> initConfig;

    @Setter
    @Getter
    private Map<ServerConfigKey, String> serverConfig;

    @Autowired
    private ConfigurableEnvironment environment;

    public String get(SystemInitConfigKey key) {
        String property = environment.getProperty(key.name());
        return StringUtils.hasText(property) ? property : initConfig.get(key);
    }

    public String get(ServerConfigKey key) {
        String property = environment.getProperty(key.name());
        return StringUtils.hasText(property) ? property : serverConfig.get(key);
    }

}

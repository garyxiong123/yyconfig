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
package apollo.pattern.factory;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * namespace配置管理仓库:  配置（namespace）工厂 管理
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Slf4j
public class DefaultConfigFactoryManager implements ConfigFactoryManager {

    @Autowired
    private ConfigFactory configFactory;

    private Map<String, ConfigFactory> m_factories = Maps.newConcurrentMap();

    public DefaultConfigFactoryManager() {
    }


    @Override
    public void register(String namespace, ConfigFactory factory) {
        if (m_factories.containsKey(namespace)) {
            log.warn("ConfigFactory({}) is overridden by {}!", namespace, factory.getClass());
        }

        m_factories.put(namespace, factory);
    }



    @Override
    public ConfigFactory getFactory(String namespace) {
        // step 1: check hacked factory
        ConfigFactory factory = m_factories.get(namespace);

        if (factory != null) {
            return factory;
        }

        // step 2: check cache
        factory = m_factories.get(namespace);

        if (factory != null) {
            return factory;
        }
        m_factories.put(namespace, configFactory);

        // factory should not be null
        return factory;
    }
}

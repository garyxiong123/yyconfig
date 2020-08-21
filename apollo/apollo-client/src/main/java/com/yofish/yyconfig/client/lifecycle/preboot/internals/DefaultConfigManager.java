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
package com.yofish.yyconfig.client.lifecycle.preboot.internals;

import com.yofish.yyconfig.client.domain.config.Config;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.ApolloInjector;
import com.yofish.yyconfig.client.pattern.listener.configfile.ConfigFile;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.factory.ConfigFactory;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.factory.ConfigFactoryManager;
import com.google.common.collect.Maps;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;

import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 * @Descripton: 配置管理（namespace纬度的， 管理 配置 和 配置文件）
 */
public class DefaultConfigManager implements ConfigManager {

    private ConfigFactoryManager m_factoryManager;

    private Map<String, Config> m_configs = Maps.newConcurrentMap();
    private Map<String, ConfigFile> m_configFiles = Maps.newConcurrentMap();

    public DefaultConfigManager() {
        m_factoryManager = ApolloInjector.getInstance(ConfigFactoryManager.class);
    }

    @Override
    public Config getOrCreateConfig(String namespace) {
        Config config = m_configs.get(namespace);

        if (config == null) {
            synchronized (this) {
                config = m_configs.get(namespace);

                if (config == null) {
                    ConfigFactory factory = m_factoryManager.getFactory(namespace);

                    config = factory.create(namespace);
                    m_configs.put(namespace, config);
                }
            }
        }

        return config;
    }

    @Override
    public ConfigFile getOrCreateConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        String namespaceFileName = String.format("%s.%s", namespace, configFileFormat.getValue());
        ConfigFile configFile = m_configFiles.get(namespaceFileName);

        if (configFile == null) {
            synchronized (this) {
                configFile = m_configFiles.get(namespaceFileName);

                if (configFile == null) {
                    ConfigFactory factory = m_factoryManager.getFactory(namespaceFileName);

                    configFile = factory.createConfigFile(namespaceFileName, configFileFormat);
                    m_configFiles.put(namespaceFileName, configFile);
                }
            }
        }

        return configFile;
    }
}

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
package com.yofish.yyconfig.client.lifecycle.preboot;


import com.yofish.yyconfig.client.domain.config.Config;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.ApolloInjector;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ConfigManager;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.factory.ConfigFactory;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.factory.ConfigFactoryManager;
import com.yofish.yyconfig.client.pattern.listener.configfile.ConfigFile;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;

/**
 * Entry point for client config use
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigService {
    private static final ConfigService s_instance = new ConfigService();

    private volatile ConfigManager m_configManager;

    private volatile ConfigFactoryManager m_factoryManager;

    private ConfigManager getManager() {
        if (m_configManager == null) {
            synchronized (this) {
                if (m_configManager == null) {
                    m_configManager = ApolloInjector.getInstance(ConfigManager.class);
                }
            }
        }

        return m_configManager;
    }

    private ConfigFactoryManager getConfigFactoryManager() {
        if (m_factoryManager == null) {
            synchronized (this) {
                if (m_factoryManager == null) {
                    m_factoryManager = ApolloInjector.getInstance(ConfigFactoryManager.class);
                }
            }
        }
        return m_factoryManager;
    }

    /**
     * Get Application's config instance.
     *
     * @return config instance
     */
    public static Config getAppConfig() {
        return getConfig(ConfigConsts.NAMESPACE_APPLICATION);
    }

    /**
     * Get the config instance for the appNamespace.
     *
     * @param namespace the appNamespace of the config
     * @return config instance
     */
    public static Config getConfig(String namespace) {
        return s_instance.getManager().getOrCreateConfig(namespace);
    }

    public static ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        return s_instance.getManager().getOrCreateConfigFile(namespace, configFileFormat);
    }

    static void setConfig(Config config) {
        setConfig(ConfigConsts.NAMESPACE_APPLICATION, config);
    }

    /**
     * Manually set the config for the appNamespace specified, use with caution.
     *
     * @param namespace the appNamespace
     * @param config    the config instance
     */
    static void setConfig(String namespace, final Config config) {
        s_instance.getConfigFactoryManager().register(namespace, new ConfigFactory() {
            @Override
            public Config create(String namespace) {
                return config;
            }

            @Override
            public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
                return null;
            }

        });
    }

    static void setConfigFactory(ConfigFactory factory) {
        setConfigFactory(ConfigConsts.NAMESPACE_APPLICATION, factory);
    }

    /**
     * Manually set the config factory for the appNamespace specified, use with caution.
     *
     * @param namespace the appNamespace
     * @param factory   the factory instance
     */
    static void setConfigFactory(String namespace, ConfigFactory factory) {
        s_instance.getConfigFactoryManager().register(namespace, factory);
    }

    // for test only
    static void reset() {
        synchronized (s_instance) {
            s_instance.m_configManager = null;
            s_instance.m_factoryManager = null;
        }
    }
}

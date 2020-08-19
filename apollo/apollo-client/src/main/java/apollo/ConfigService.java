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
package apollo;


import apollo.domain.config.Config;
import apollo.internals.ConfigManager;
import apollo.pattern.factory.ConfigFactory;
import apollo.pattern.factory.ConfigFactoryManager;
import apollo.pattern.listener.ConfigFile;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.ConfigFileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Entry point for client config use
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class ConfigService {

    @Autowired
    private ConfigManager m_configManager;
    @Autowired
    private ConfigFactoryManager configFactoryManager;

    private ConfigManager getManager() {

        return m_configManager;
    }


    /**
     * Get Application's config instance.
     *
     * @return config instance
     */
    public Config getAppConfig() {
        return getConfig(ConfigConsts.NAMESPACE_APPLICATION);
    }

    /**
     * Get the config instance for the appNamespace.
     *
     * @param namespace the appNamespace of the config
     * @return config instance
     */
    public Config getConfig(String namespace) {
        return this.getManager().getOrCreateConfig(namespace);
    }

    public ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        return this.getManager().getOrCreateConfigFile(namespace, configFileFormat);
    }

    void setConfig(Config config) {
        setConfig(ConfigConsts.NAMESPACE_APPLICATION, config);
    }

    /**
     * Manually set the config for the appNamespace specified, use with caution.
     *
     * @param namespace the appNamespace
     * @param config    the config instance
     */
    void setConfig(String namespace, final Config config) {
        configFactoryManager.register(namespace, new ConfigFactory() {
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

    void setConfigFactory(ConfigFactory factory) {
        setConfigFactory(ConfigConsts.NAMESPACE_APPLICATION, factory);
    }

    /**
     * Manually set the config factory for the appNamespace specified, use with caution.
     *
     * @param namespace the appNamespace
     * @param factory   the factory instance
     */
    void setConfigFactory(String namespace, ConfigFactory factory) {
        configFactoryManager.register(namespace, factory);
    }

    // for test only
    void reset() {
        synchronized (this) {
            this.m_configManager = null;
        }
    }
}

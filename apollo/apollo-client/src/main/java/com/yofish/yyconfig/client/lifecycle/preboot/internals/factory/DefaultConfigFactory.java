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
package com.yofish.yyconfig.client.lifecycle.preboot.internals.factory;

import com.yofish.yyconfig.client.domain.config.Config;
import com.yofish.yyconfig.client.domain.config.DefaultConfig;
import com.yofish.yyconfig.client.domain.configfile.*;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.ApolloInjector;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ClientConfig;
import com.yofish.yyconfig.client.pattern.listener.configfile.ConfigFile;
import com.yofish.yyconfig.client.repository.ConfigRepository;
import com.yofish.yyconfig.client.repository.LocalFileConfigRepository;
import com.yofish.yyconfig.client.repository.RemoteConfigRepository;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfigFactory implements ConfigFactory {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConfigFactory.class);
    private ClientConfig m_Client_config;

    public DefaultConfigFactory() {
        m_Client_config = ApolloInjector.getInstance(ClientConfig.class);
    }

    @Override
    public Config create(String namespace) {
        DefaultConfig defaultConfig = new DefaultConfig(namespace, createLocalConfigRepository(namespace));
        return defaultConfig;
    }

    @Override
    public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        ConfigRepository configRepository = createLocalConfigRepository(namespace);
        switch (configFileFormat) {
            case Properties:
                return new PropertiesConfigFile(namespace, configRepository);
            case XML:
                return new XmlConfigFile(namespace, configRepository);
            case JSON:
                return new JsonConfigFile(namespace, configRepository);
            case YAML:
                return new YamlConfigFile(namespace, configRepository);
            case YML:
                return new YmlConfigFile(namespace, configRepository);
        }

        return null;
    }

    LocalFileConfigRepository createLocalConfigRepository(String namespace) {
        if (m_Client_config.isInLocalMode()) {
            logger.warn(
                    "==== Apollo is in local mode! Won't pull configs from remote server for appNamespace {} ! ====",
                    namespace);
            return new LocalFileConfigRepository(namespace);
        }
        return new LocalFileConfigRepository(namespace, createRemoteConfigRepository(namespace));
    }

    RemoteConfigRepository createRemoteConfigRepository(String namespace) {
        return new RemoteConfigRepository(namespace);
    }
}

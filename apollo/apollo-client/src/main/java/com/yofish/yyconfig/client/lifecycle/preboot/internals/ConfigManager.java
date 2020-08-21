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
import com.yofish.yyconfig.client.pattern.listener.configfile.ConfigFile;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigManager {
  /**
   * Get the config instance for the appNamespace specified.
   * @param namespace the appNamespace
   * @return the config instance for the appNamespace
   */
  public Config getOrCreateConfig(String namespace);

  /**
   * Get the config file instance for the appNamespace specified.
   * @param namespace the appNamespace
   * @param configFileFormat the config file format
   * @return the config file instance for the appNamespace
   */
  public ConfigFile getOrCreateConfigFile(String namespace, ConfigFileFormat configFileFormat);
}

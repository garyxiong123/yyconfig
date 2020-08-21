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
package com.yofish.yyconfig.client.pattern.listener.configfile;


import com.yofish.yyconfig.client.enums.ConfigSourceType;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigFile extends ConfigFileChangePublisher {
  /**
   * Get file content of the appNamespace
   * @return file content, {@code null} if there is no content
   */
  String getContent();

  /**
   * Whether the config file has any content
   * @return true if it has content, false otherwise.
   */
  boolean hasContent();

  /**
   * Get the appNamespace of this config file instance
   * @return the appNamespace
   */
  String getNamespace();

  /**
   * Get the file format of this config file instance
   * @return the config file format enum
   */
  ConfigFileFormat getConfigFileFormat();



  /**
   * Return the config's source type, i.e. where is the config loaded from
   *
   * @return the config's source type
   */
  public ConfigSourceType getSourceType();
}

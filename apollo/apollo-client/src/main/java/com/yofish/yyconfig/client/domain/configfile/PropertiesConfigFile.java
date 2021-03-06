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
package com.yofish.yyconfig.client.domain.configfile;

import com.yofish.yyconfig.client.component.exceptions.ApolloConfigException;
import com.yofish.yyconfig.client.repository.ConfigRepository;
import com.yofish.yyconfig.client.component.util.ExceptionUtil;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;
import com.yofish.yyconfig.common.framework.apollo.core.utils.PropertiesUtil;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class PropertiesConfigFile extends AbstractConfigFile {
  private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigFile.class);
  protected AtomicReference<String> m_contentCache;

  public PropertiesConfigFile(String namespace,
                              ConfigRepository configRepository) {
    super(namespace, configRepository);
    m_contentCache = new AtomicReference<>();
  }

  @Override
  protected void update(Properties newProperties) {
    m_configProperties.set(newProperties);
    m_contentCache.set(null);
  }

  @Override
  public String getContent() {
    if (m_contentCache.get() == null) {
      m_contentCache.set(doGetContent());
    }
    return m_contentCache.get();
  }

  String doGetContent() {
    if (!this.hasContent()) {
      return null;
    }

    try {
      return PropertiesUtil.toString(m_configProperties.get());
    } catch (Throwable ex) {
      ApolloConfigException exception =
          new ApolloConfigException(String
              .format("Parse properties file content failed for appNamespace: %s, cause: %s",
                  m_namespace, ExceptionUtil.getDetailMessage(ex)));
      Tracer.logError(exception);
      throw exception;
    }
  }

  @Override
  public boolean hasContent() {
    return m_configProperties.get() != null && !m_configProperties.get().isEmpty();
  }

  @Override
  public ConfigFileFormat getConfigFileFormat() {
    return ConfigFileFormat.Properties;
  }

}

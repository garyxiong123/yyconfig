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
package com.yofish.yyconfig.common.framework.foundation.spi.provider;

import java.io.InputStream;

/**
 * Provider for application related properties
 */
public interface ApplicationProvider extends Provider {
  /**
   * @return the application's app id
   */
  public String getAppId();

  /**
   * @return whether the application's app id is set or not
   */
  public boolean isAppIdSet();

  /**
   * Initialize the application provider with the specified input stream
   */
  public void initialize(InputStream in);
}

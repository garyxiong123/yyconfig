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
package framework.foundation.spi.provider;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provider for server related properties
 */
public interface ServerProvider extends Provider {
  /**
   * @return current environment or {@code null} if not set
   */
  public String getEnvType();

  /**
   * @return whether current environment is set or not
   */
  public boolean isEnvTypeSet();

  /**
   * @return current data center or {@code null} if not set
   */
  public String getDataCenter();

  /**
   * @return whether data center is set or not
   */
  public boolean isDataCenterSet();

  /**
   * Initialize server provider with the specified input stream
   *
   * @throws IOException
   */
  public void initialize(InputStream in) throws IOException;
}

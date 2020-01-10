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
package apollo.internals;

import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.Env;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultMetaServerProviderTest {

  @After
  public void tearDown() throws Exception {
    System.clearProperty(ConfigConsts.APOLLO_META_KEY);
  }

  @Test
  public void testWithSystemProperty() throws Exception {
    String someMetaAddress = "someMetaAddress";
    Env someEnv = Env.DEV;

    System.setProperty(ConfigConsts.APOLLO_META_KEY, " " + someMetaAddress + " ");

    DefaultMetaServerProvider defaultMetaServerProvider = new DefaultMetaServerProvider();

    assertEquals(someMetaAddress, defaultMetaServerProvider.getMetaServerAddress(someEnv));
  }

  @Test
  public void testWithNoSystemProperty() throws Exception {
    Env someEnv = Env.DEV;

    DefaultMetaServerProvider defaultMetaServerProvider = new DefaultMetaServerProvider();

    assertNull(defaultMetaServerProvider.getMetaServerAddress(someEnv));
  }
}

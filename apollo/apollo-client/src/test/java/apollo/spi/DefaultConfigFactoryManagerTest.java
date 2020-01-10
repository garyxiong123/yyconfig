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
package apollo.spi;

import apollo.Config;
import apollo.ConfigFile;
import apollo.build.MockInjector;
import framework.apollo.core.enums.ConfigFileFormat;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigFactoryManagerTest {
  private DefaultConfigFactoryManager defaultConfigFactoryManager;

  @Before
  public void setUp() throws Exception {
    MockInjector.reset();
    MockInjector.setInstance(ConfigRegistry.class, new MockConfigRegistry());
    defaultConfigFactoryManager = new DefaultConfigFactoryManager();
  }

  @Test
  public void testGetFactoryFromRegistry() throws Exception {
    ConfigFactory result =
        defaultConfigFactoryManager.getFactory(MockConfigRegistry.NAMESPACE_REGISTERED);

    assertEquals(MockConfigRegistry.REGISTERED_CONFIGFACTORY, result);
  }

  @Test
  public void testGetFactoryFromNamespace() throws Exception {
    String someNamespace = "someName";
    MockInjector.setInstance(ConfigFactory.class, someNamespace, new SomeConfigFactory());

    ConfigFactory result = defaultConfigFactoryManager.getFactory(someNamespace);

    assertThat("When appNamespace is registered, should return the registerd config factory", result,
        instanceOf(SomeConfigFactory.class));
  }

  @Test
  public void testGetFactoryFromNamespaceMultipleTimes() throws Exception {
    String someNamespace = "someName";
    MockInjector.setInstance(ConfigFactory.class, someNamespace, new SomeConfigFactory());

    ConfigFactory result = defaultConfigFactoryManager.getFactory(someNamespace);
    ConfigFactory anotherResult = defaultConfigFactoryManager.getFactory(someNamespace);

    assertThat(
        "Get config factory with the same appNamespace multiple times should returnt the same instance",
        anotherResult, equalTo(result));
  }

  @Test
  public void testGetFactoryFromDefault() throws Exception {
    String someNamespace = "someName";
    MockInjector.setInstance(ConfigFactory.class, new AnotherConfigFactory());

    ConfigFactory result = defaultConfigFactoryManager.getFactory(someNamespace);

    assertThat("When appNamespace is not registered, should return the default config factory", result,
        instanceOf(AnotherConfigFactory.class));
  }

  public static class MockConfigRegistry implements ConfigRegistry {
    public static String NAMESPACE_REGISTERED = "some-appNamespace-registered";
    public static ConfigFactory REGISTERED_CONFIGFACTORY = new ConfigFactory() {
      @Override
      public Config create(String namespace) {
        return null;
      }

      @Override
      public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        return null;
      }

    };

    @Override
    public void register(String namespace, ConfigFactory factory) {
      //do nothing
    }

    @Override
    public ConfigFactory getFactory(String namespace) {
      if (namespace.equals(NAMESPACE_REGISTERED)) {
        return REGISTERED_CONFIGFACTORY;
      }
      return null;
    }
  }

  public static class SomeConfigFactory implements ConfigFactory {
    @Override
    public Config create(String namespace) {
      return null;
    }

    @Override
    public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
      return null;
    }
  }

  public static class AnotherConfigFactory implements ConfigFactory {
    @Override
    public Config create(String namespace) {
      return null;
    }

    @Override
    public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
      return null;
    }
  }

}

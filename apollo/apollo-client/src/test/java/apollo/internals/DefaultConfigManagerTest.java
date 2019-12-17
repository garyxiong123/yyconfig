package apollo.internals;

import apollo.Config;
import apollo.ConfigFile;
import apollo.build.MockInjector;
import apollo.enums.ConfigSourceType;
import apollo.spi.ConfigFactory;
import apollo.spi.ConfigFactoryManager;
import apollo.util.ConfigUtil;
import framework.apollo.core.enums.ConfigFileFormat;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.Set;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigManagerTest {
  private DefaultConfigManager defaultConfigManager;
  private static String someConfigContent;

  @Before
  public void setUp() throws Exception {
    MockInjector.reset();
    MockInjector.setInstance(ConfigFactoryManager.class, new MockConfigFactoryManager());
    MockInjector.setInstance(ConfigUtil.class, new ConfigUtil());
    defaultConfigManager = new DefaultConfigManager();
    someConfigContent = "someContent";
  }

  @Test
  public void testGetConfig() throws Exception {
    String someNamespace = "someName";
    String anotherNamespace = "anotherName";
    String someKey = "someKey";
    Config config = defaultConfigManager.getOrCreateConfig(someNamespace);
    Config anotherConfig = defaultConfigManager.getOrCreateConfig(anotherNamespace);

    assertEquals(someNamespace + ":" + someKey, config.getProperty(someKey, null));
    assertEquals(anotherNamespace + ":" + someKey, anotherConfig.getProperty(someKey, null));
  }

  @Test
  public void testGetConfigMultipleTimesWithSameNamespace() throws Exception {
    String someNamespace = "someName";
    Config config = defaultConfigManager.getOrCreateConfig(someNamespace);
    Config anotherConfig = defaultConfigManager.getOrCreateConfig(someNamespace);

    assertThat(
        "Get config multiple times with the same appNamespace should return the same config instance",
        config, equalTo(anotherConfig));
  }

  @Test
  public void testGetConfigFile() throws Exception {
    String someNamespace = "someName";
    ConfigFileFormat someConfigFileFormat = ConfigFileFormat.Properties;

    ConfigFile configFile =
        defaultConfigManager.getOrCreateConfigFile(someNamespace, someConfigFileFormat);

    assertEquals(someConfigFileFormat, configFile.getConfigFileFormat());
    assertEquals(someConfigContent, configFile.getContent());
  }

  @Test
  public void testGetConfigFileMultipleTimesWithSameNamespace() throws Exception {
    String someNamespace = "someName";
    ConfigFileFormat someConfigFileFormat = ConfigFileFormat.Properties;

    ConfigFile someConfigFile =
        defaultConfigManager.getOrCreateConfigFile(someNamespace, someConfigFileFormat);
    ConfigFile anotherConfigFile =
        defaultConfigManager.getOrCreateConfigFile(someNamespace, someConfigFileFormat);

    assertThat(
        "Get config file multiple times with the same appNamespace should return the same config file instance",
        someConfigFile, equalTo(anotherConfigFile));

  }

  public static class MockConfigFactoryManager implements ConfigFactoryManager {

    @Override
    public ConfigFactory getFactory(String namespace) {
      return new ConfigFactory() {
        @Override
        public Config create(final String namespace) {
          return new AbstractConfig() {
            @Override
            public void onRepositoryChange(String namespace, Properties newProperties) {

            }

            @Override
            public String getProperty(String key, String defaultValue) {
              return namespace + ":" + key;
            }

            @Override
            public Set<String> getPropertyNames() {
              return null;
            }

            @Override
            public ConfigSourceType getSourceType() {
              return null;
            }
          };
        }

        @Override
        public ConfigFile createConfigFile(String namespace, final ConfigFileFormat configFileFormat) {
          ConfigRepository someConfigRepository = mock(ConfigRepository.class);
          return new AbstractConfigFile(namespace, someConfigRepository) {

            @Override
            protected void update(Properties newProperties) {

            }

            @Override
            public String getContent() {
              return someConfigContent;
            }

            @Override
            public boolean hasContent() {
              return true;
            }

            @Override
            public ConfigFileFormat getConfigFileFormat() {
              return configFileFormat;
            }
          };
        }
      };
    }
  }
}

package apollo.internals;

import apollo.Config;
import apollo.ConfigFile;
import apollo.build.ApolloInjector;
import apollo.spi.ConfigFactory;
import apollo.spi.ConfigFactoryManager;
import com.google.common.collect.Maps;
import framework.apollo.core.enums.ConfigFileFormat;

import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigManager implements ConfigManager {
  private ConfigFactoryManager m_factoryManager;

  private Map<String, Config> m_configs = Maps.newConcurrentMap();
  private Map<String, ConfigFile> m_configFiles = Maps.newConcurrentMap();

  public DefaultConfigManager() {
    m_factoryManager = ApolloInjector.getInstance(ConfigFactoryManager.class);
  }

  @Override
  public Config getOrCreateConfig(String namespace) {
    Config config = m_configs.get(namespace);

    if (config == null) {
      synchronized (this) {
        config = m_configs.get(namespace);

        if (config == null) {
          ConfigFactory factory = m_factoryManager.getFactory(namespace);

          config = factory.create(namespace);
          m_configs.put(namespace, config);
        }
      }
    }

    return config;
  }

  @Override
  public ConfigFile getOrCreateConfigFile(String namespace, ConfigFileFormat configFileFormat) {
    String namespaceFileName = String.format("%s.%s", namespace, configFileFormat.getValue());
    ConfigFile configFile = m_configFiles.get(namespaceFileName);

    if (configFile == null) {
      synchronized (this) {
        configFile = m_configFiles.get(namespaceFileName);

        if (configFile == null) {
          ConfigFactory factory = m_factoryManager.getFactory(namespaceFileName);

          configFile = factory.createConfigFile(namespaceFileName, configFileFormat);
          m_configFiles.put(namespaceFileName, configFile);
        }
      }
    }

    return configFile;
  }
}

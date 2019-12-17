package apollo;


import apollo.build.ApolloInjector;
import apollo.internals.ConfigManager;
import apollo.spi.ConfigFactory;
import apollo.spi.ConfigRegistry;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.ConfigFileFormat;

/**
 * Entry point for client config use
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigService {
  private static final ConfigService s_instance = new ConfigService();

  private volatile ConfigManager m_configManager;
  private volatile ConfigRegistry m_configRegistry;

  private ConfigManager getManager() {
    if (m_configManager == null) {
      synchronized (this) {
        if (m_configManager == null) {
          m_configManager = ApolloInjector.getInstance(ConfigManager.class);
        }
      }
    }

    return m_configManager;
  }

  private ConfigRegistry getRegistry() {
    if (m_configRegistry == null) {
      synchronized (this) {
        if (m_configRegistry == null) {
          m_configRegistry = ApolloInjector.getInstance(ConfigRegistry.class);
        }
      }
    }

    return m_configRegistry;
  }

  /**
   * Get Application's config instance.
   *
   * @return config instance
   */
  public static Config getAppConfig() {
    return getConfig(ConfigConsts.NAMESPACE_APPLICATION);
  }

  /**
   * Get the config instance for the appNamespace.
   *
   * @param namespace the appNamespace of the config
   * @return config instance
   */
  public static Config getConfig(String namespace) {
    return s_instance.getManager().getOrCreateConfig(namespace);
  }

  public static ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat) {
    return s_instance.getManager().getOrCreateConfigFile(namespace, configFileFormat);
  }

  static void setConfig(Config config) {
    setConfig(ConfigConsts.NAMESPACE_APPLICATION, config);
  }

  /**
   * Manually set the config for the appNamespace specified, use with caution.
   *
   * @param namespace the appNamespace
   * @param config    the config instance
   */
  static void setConfig(String namespace, final Config config) {
    s_instance.getRegistry().register(namespace, new ConfigFactory() {
      @Override
      public Config create(String namespace) {
        return config;
      }

      @Override
      public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        return null;
      }

    });
  }

  static void setConfigFactory(ConfigFactory factory) {
    setConfigFactory(ConfigConsts.NAMESPACE_APPLICATION, factory);
  }

  /**
   * Manually set the config factory for the appNamespace specified, use with caution.
   *
   * @param namespace the appNamespace
   * @param factory   the factory instance
   */
  static void setConfigFactory(String namespace, ConfigFactory factory) {
    s_instance.getRegistry().register(namespace, factory);
  }

  // for test only
  static void reset() {
    synchronized (s_instance) {
      s_instance.m_configManager = null;
      s_instance.m_configRegistry = null;
    }
  }
}

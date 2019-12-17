package apollo.spi;

import apollo.Config;
import apollo.ConfigFile;
import apollo.build.ApolloInjector;
import apollo.internals.*;
import apollo.util.ConfigUtil;
import framework.apollo.core.enums.ConfigFileFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigFactory implements ConfigFactory {
  private static final Logger logger = LoggerFactory.getLogger(DefaultConfigFactory.class);
  private ConfigUtil m_configUtil;

  public DefaultConfigFactory() {
    m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
  }

  @Override
  public Config create(String namespace) {
    DefaultConfig defaultConfig =
        new DefaultConfig(namespace, createLocalConfigRepository(namespace));
    return defaultConfig;
  }

  @Override
  public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
    ConfigRepository configRepository = createLocalConfigRepository(namespace);
    switch (configFileFormat) {
      case Properties:
        return new PropertiesConfigFile(namespace, configRepository);
      case XML:
        return new XmlConfigFile(namespace, configRepository);
      case JSON:
        return new JsonConfigFile(namespace, configRepository);
      case YAML:
        return new YamlConfigFile(namespace, configRepository);
      case YML:
        return new YmlConfigFile(namespace, configRepository);
    }

    return null;
  }

  LocalFileConfigRepository createLocalConfigRepository(String namespace) {
    if (m_configUtil.isInLocalMode()) {
      logger.warn(
          "==== Apollo is in local mode! Won't pull configs from remote server for appNamespace {} ! ====",
          namespace);
      return new LocalFileConfigRepository(namespace);
    }
    return new LocalFileConfigRepository(namespace, createRemoteConfigRepository(namespace));
  }

  RemoteConfigRepository createRemoteConfigRepository(String namespace) {
    return new RemoteConfigRepository(namespace);
  }
}

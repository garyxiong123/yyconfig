package apollo.internals;


import apollo.Config;
import apollo.ConfigFile;
import framework.apollo.core.enums.ConfigFileFormat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigManager {
  /**
   * Get the config instance for the appNamespace specified.
   * @param namespace the appNamespace
   * @return the config instance for the appNamespace
   */
  public Config getOrCreateConfig(String namespace);

  /**
   * Get the config file instance for the appNamespace specified.
   * @param namespace the appNamespace
   * @param configFileFormat the config file format
   * @return the config file instance for the appNamespace
   */
  public ConfigFile getOrCreateConfigFile(String namespace, ConfigFileFormat configFileFormat);
}

package apollo.internals;


import apollo.Config;
import apollo.ConfigFile;
import framework.apollo.core.enums.ConfigFileFormat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigManager {
  /**
   * Get the config instance for the namespace specified.
   * @param namespace the namespace
   * @return the config instance for the namespace
   */
  public Config getConfig(String namespace);

  /**
   * Get the config file instance for the namespace specified.
   * @param namespace the namespace
   * @param configFileFormat the config file format
   * @return the config file instance for the namespace
   */
  public ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat);
}

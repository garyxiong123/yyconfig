package apollo.spi;


import apollo.Config;
import apollo.ConfigFile;
import framework.apollo.core.enums.ConfigFileFormat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigFactory {
  /**
   * Create the config instance for the appNamespace.
   *
   * @param namespace the appNamespace
   * @return the newly created config instance
   */
  public Config create(String namespace);

  /**
   * Create the config file instance for the appNamespace
   * @param namespace the appNamespace
   * @return the newly created config file instance
   */
  public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat);
}

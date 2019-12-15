package apollo;


import apollo.enums.ConfigSourceType;
import apollo.util.ConfigFileChangePublisher;
import framework.apollo.core.enums.ConfigFileFormat;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigFile extends ConfigFileChangePublisher {
  /**
   * Get file content of the appNamespace
   * @return file content, {@code null} if there is no content
   */
  String getContent();

  /**
   * Whether the config file has any content
   * @return true if it has content, false otherwise.
   */
  boolean hasContent();

  /**
   * Get the appNamespace of this config file instance
   * @return the appNamespace
   */
  String getNamespace();

  /**
   * Get the file format of this config file instance
   * @return the config file format enum
   */
  ConfigFileFormat getConfigFileFormat();



  /**
   * Return the config's source type, i.e. where is the config loaded from
   *
   * @return the config's source type
   */
  public ConfigSourceType getSourceType();
}

package apollo.spi;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigFactoryManager {
  /**
   * Get the config factory for the appNamespace.
   *
   * @param namespace the appNamespace
   * @return the config factory for this appNamespace
   */
  public ConfigFactory getFactory(String namespace);
}

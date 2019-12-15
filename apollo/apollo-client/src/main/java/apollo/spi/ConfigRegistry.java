package apollo.spi;

/**
 * The manually config registry, use with caution!
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigRegistry {
  /**
   * Register the config factory for the appNamespace specified.
   *
   * @param namespace the appNamespace
   * @param factory   the factory for this appNamespace
   */
  public void register(String namespace, ConfigFactory factory);

  /**
   * Get the registered config factory for the appNamespace.
   *
   * @param namespace the appNamespace
   * @return the factory registered for this appNamespace
   */
  public ConfigFactory getFactory(String namespace);
}

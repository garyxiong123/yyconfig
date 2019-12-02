package apollo.internals;


import apollo.enums.ConfigSourceType;

import java.util.Properties;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigRepository extends RepositoryChangePublisher{
  /**
   * Get the config from this repository.
   * @return config
   */
  public Properties getConfig();

  /**
   * Set the fallback repo for this repository.
   * @param upstreamConfigRepository the upstream repo
   */
  public void setUpstreamRepository(ConfigRepository upstreamConfigRepository);



  /**
   * Return the config's source type, i.e. where is the config loaded from
   *
   * @return the config's source type
   */
  public ConfigSourceType getSourceType();
}

package apollo.internals;

import apollo.model.ConfigFileChangeEvent;

import java.util.Properties;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface RepositoryChangeListener {
  /**
   * Invoked when config repository changes.
   * @param namespace the appNamespace of this repository change
   * @param newProperties the properties after change
   */
  public void onRepositoryChange(String namespace, Properties newProperties);

}

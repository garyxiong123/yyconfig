package apollo;


import apollo.model.ConfigChangeEvent;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigChangeListener {
  /**
   * Invoked when there is any config change for the appNamespace.
   * @param changeEvent the event for this change
   */
  public void onChange(ConfigChangeEvent changeEvent);
}

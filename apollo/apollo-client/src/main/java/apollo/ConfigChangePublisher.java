package apollo;

import apollo.model.ConfigChangeEvent;

import java.util.Set;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/29 下午4:07
 */
public interface ConfigChangePublisher {

    public void fireConfigChange(final ConfigChangeEvent changeEvent);


    public void addChangeListener(ConfigChangeListener listener);

    /**
     * Add change listener to this config instance, will only be notified when any of the interested keys is changed in this appNamespace.
     *
     * @param listener       the config change listener
     * @param interestedKeys the keys interested by the listener
     * @since 1.0.0
     */
    public void addChangeListener(ConfigChangeListener listener, Set<String> interestedKeys);

    /**
     * Remove the change listener
     *
     * @param listener the specific config change listener to remove
     * @return true if the specific config change listener is found and removed
     * @since 1.1.0
     */
    public boolean removeChangeListener(ConfigChangeListener listener);
}

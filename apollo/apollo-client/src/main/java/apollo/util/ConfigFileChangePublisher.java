package apollo.util;

import apollo.ConfigChangeListener;
import apollo.ConfigFileChangeListener;
import apollo.model.ConfigFileChangeEvent;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/3 下午1:35
 */
public interface ConfigFileChangePublisher {

    void fireConfigChange(final ConfigFileChangeEvent changeEvent);


    /**
     * Add change listener to this config file instance.
     *
     * @param listener the config file change listener
     */
    void addChangeListener(ConfigFileChangeListener listener);

    /**
     * Remove the change listener
     *
     * @param listener the specific config change listener to remove
     * @return true if the specific config change listener is found and removed
     */
    public boolean removeChangeListener(ConfigChangeListener listener);
}

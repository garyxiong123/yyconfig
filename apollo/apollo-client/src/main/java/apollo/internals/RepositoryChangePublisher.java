package apollo.internals;

import java.util.List;
import java.util.Properties;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/29 下午3:34
 */
public interface RepositoryChangePublisher {


    void fireRepositoryChange(String namespace, Properties newProperties);

    /**
     * Add change listener.
     *
     * @param listener the listener to observe the changes
     */
     void addChangeListener(RepositoryChangeListener listener);

    /**
     * Remove change listener.
     *
     * @param listener the listener to remove
     */
     void removeChangeListener(RepositoryChangeListener listener);
}

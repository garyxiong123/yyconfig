/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.yyconfig.client.pattern.listener.config;

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

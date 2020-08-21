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
package com.yofish.yyconfig.client.pattern.listener.configfile;

import com.yofish.yyconfig.client.pattern.listener.config.ConfigChangeListener;

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

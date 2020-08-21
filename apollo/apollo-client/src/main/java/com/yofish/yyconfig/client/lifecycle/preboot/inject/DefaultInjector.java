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
package com.yofish.yyconfig.client.lifecycle.preboot.inject;


import com.yofish.yyconfig.client.component.exceptions.ApolloConfigException;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.beans.ApolloModule;
import com.google.inject.Guice;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;

/**
 * Guice injector
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultInjector implements Injector {
    private com.google.inject.Injector m_injector;

    public DefaultInjector() {
        try {
            m_injector = Guice.createInjector(new ApolloModule());
        } catch (Throwable ex) {
            ApolloConfigException exception = new ApolloConfigException("Unable to initialize Guice Injector!", ex);
            Tracer.logError(exception);
            throw exception;
        }
    }

    @Override
    public <T> T getInstance(Class<T> clazz) {
        try {
            return m_injector.getInstance(clazz);
        } catch (Throwable ex) {
            Tracer.logError(ex);
            throw new ApolloConfigException(
                    String.format("Unable to load instance for %s!", clazz.getName()), ex);
        }
    }

    @Override
    public <T> T getInstance(Class<T> clazz, String name) {
        //Guice does not support get instance by type and name
        return null;
    }

}

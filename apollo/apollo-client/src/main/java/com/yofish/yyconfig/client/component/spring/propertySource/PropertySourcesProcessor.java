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
package com.yofish.yyconfig.client.component.spring.propertySource;

import com.yofish.yyconfig.client.domain.config.Config;
import com.yofish.yyconfig.client.lifecycle.preboot.ConfigService;
import com.yofish.yyconfig.client.component.constant.PropertySourcesConstants;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.ApolloInjector;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.SpringInjector;
import com.yofish.yyconfig.client.pattern.listener.config.AutoUpdateConfigChangeListener;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ClientConfig;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Apollo Property Sources processor for Spring Annotation Based Application. <br /> <br />
 * <p>
 * The reason why PropertySourcesProcessor implements {@link BeanFactoryPostProcessor} instead of
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor} is that lower versions of
 * Spring (e.g. 3.1.1) doesn't support registering BeanDefinitionRegistryPostProcessor in ImportBeanDefinitionRegistrar
 * - {@link com.ctrip.framework.apollo.spring.annotation.ApolloConfigRegistrar}
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {
    private static final Multimap<Integer, String> NAMESPACE_NAMES = LinkedHashMultimap.create();
    private static final Set<BeanFactory> AUTO_UPDATE_INITIALIZED_BEAN_FACTORIES = Sets.newConcurrentHashSet();

    private ConfigPropertySourceFactory configPropertySourceFactory = SpringInjector.getInstance(ConfigPropertySourceFactory.class);
    private ConfigService configService = ApolloInjector.getInstance(ConfigService.class);
    private ClientConfig clientConfig = ApolloInjector.getInstance(ClientConfig.class);
    private ConfigurableEnvironment environment;

    public static boolean addNamespaces(Collection<String> namespaces, int order) {
        return NAMESPACE_NAMES.putAll(order, namespaces);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        initializePropertySources();
        initializeAutoUpdatePropertiesFeature(beanFactory);
    }

    private void initializePropertySources() {
        if (environment.getPropertySources().contains(PropertySourcesConstants.APOLLO_PROPERTY_SOURCE_NAME)) {
            //already initialized
            return;
        }
        CompositePropertySource composite = new CompositePropertySource(PropertySourcesConstants.APOLLO_PROPERTY_SOURCE_NAME);

        //sort by order asc
        ImmutableSortedSet<Integer> orders = ImmutableSortedSet.copyOf(NAMESPACE_NAMES.keySet());
        Iterator<Integer> iterator = orders.iterator();

        while (iterator.hasNext()) {
            int order = iterator.next();
            for (String namespace : NAMESPACE_NAMES.get(order)) {
                Config config = ConfigService.getConfig(namespace);

                composite.addPropertySource(configPropertySourceFactory.getConfigPropertySource(namespace, config));
            }
        }

        // clean up
        NAMESPACE_NAMES.clear();

        // add after the bootstrap property source or to the first
        if (environment.getPropertySources()
                .contains(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME)) {

            // ensure ApolloBootstrapPropertySources is still the first
            ensureBootstrapPropertyPrecedence(environment);

            environment.getPropertySources()
                    .addAfter(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME, composite);
        } else {
            environment.getPropertySources().addFirst(composite);
        }
    }

    private void ensureBootstrapPropertyPrecedence(ConfigurableEnvironment environment) {
        MutablePropertySources propertySources = environment.getPropertySources();

        PropertySource<?> bootstrapPropertySource = propertySources
                .get(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME);

        // not exists or already in the first place
        if (bootstrapPropertySource == null || propertySources.precedenceOf(bootstrapPropertySource) == 0) {
            return;
        }

        propertySources.remove(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME);
        propertySources.addFirst(bootstrapPropertySource);
    }

    private void initializeAutoUpdatePropertiesFeature(ConfigurableListableBeanFactory beanFactory) {
        if (!clientConfig.isAutoUpdateInjectedSpringPropertiesEnabled() ||
                !AUTO_UPDATE_INITIALIZED_BEAN_FACTORIES.add(beanFactory)) {
            return;
        }

        AutoUpdateConfigChangeListener autoUpdateConfigChangeListener = new AutoUpdateConfigChangeListener(
                environment, beanFactory);

        List<ConfigPropertySource> configPropertySources = configPropertySourceFactory.getAllConfigPropertySources();
        for (ConfigPropertySource configPropertySource : configPropertySources) {
            configPropertySource.addChangeListener(autoUpdateConfigChangeListener);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        //it is safe enough to cast as all known environment is derived from ConfigurableEnvironment
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public int getOrder() {
        //make it as early as possible
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
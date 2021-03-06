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
package com.yofish.yyconfig.client.lifecycle.boot;

import com.yofish.yyconfig.client.component.util.EnvironmentContext;
import com.yofish.yyconfig.client.domain.config.Config;
import com.yofish.yyconfig.client.lifecycle.preboot.ConfigService;
import com.yofish.yyconfig.client.component.spring.propertySource.ConfigPropertySourceFactory;
import com.yofish.yyconfig.client.component.constant.PropertySourcesConstants;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.SpringInjector;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initialize apollo system properties and inject the Apollo config in Spring Boot bootstrap phase
 *
 * <p>Configuration example:</p>
 * <pre class="code">
 *   # set app.id
 *   app.id = 100004458
 *   # enable apollo bootstrap config and inject 'application' appNamespace in bootstrap phase
 *   apollo.bootstrap.enabled = true
 * </pre>
 * <p>
 * or
 *
 * <pre class="code">
 *   # set app.id
 *   app.id = 100004458
 *   # enable apollo bootstrap config
 *   apollo.bootstrap.enabled = true
 *   # will inject 'application' and 'FX.apollo' namespaces in bootstrap phase
 *   apollo.bootstrap.namespaces = application,FX.apollo
 * </pre>
 * <p>
 * <p>
 * If you want to load Apollo configurations even before Logging System Initialization Phase,
 * add
 * <pre class="code">
 *   # set apollo.bootstrap.eagerLoad.enabled
 *   apollo.bootstrap.eagerLoad.enabled = true
 * </pre>
 * <p>
 * This would be very helpful when your logging configurations is set by Apollo.
 * <p>
 * for example, you have defined logback-spring.xml in your project, and you want to inject some attributes into logback-spring.xml.
 */
public class ApolloApplicationContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext>, EnvironmentPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ApolloApplicationContextInitializer.class);
    private static final Splitter NAMESPACE_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
    private static final String[] APOLLO_SYSTEM_PROPERTIES = {ConfigConsts.APOLLO_APP_ID, ConfigConsts.APOLLO_CLUSTER_KEY, ConfigConsts.APOLLO_CACHE_DIR, ConfigConsts.YYCONFIG_META_KEY};

    private final ConfigPropertySourceFactory configPropertySourceFactory = SpringInjector
            .getInstance(ConfigPropertySourceFactory.class);

    private ConfigService configService;

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();

        String enabled = environment.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, "false");
        if (!Boolean.valueOf(enabled)) {
            logger.debug("Apollo bootstrap config is not enabled for context {}, see property: ${{}}", context, PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED);
            return;
        }
        logger.debug("Apollo bootstrap config is enabled for context {}", context);

        initialize(environment);
    }


    /**
     * Initialize Apollo Configurations Just after environment is ready.
     *
     * @param environment
     */
    protected void initialize(ConfigurableEnvironment environment) {

        if (hasInitialized(environment)) {
            return;
        }
        EnvironmentContext.setEnvironment(environment);
        String namespaces = environment.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES, ConfigConsts.NAMESPACE_APPLICATION);
        logger.debug("Apollo bootstrap namespaces: {}", namespaces);
        List<String> namespaceList = NAMESPACE_SPLITTER.splitToList(namespaces);

        //获取到所有这个app的优先启动的namespace

        CompositePropertySource composite = new CompositePropertySource(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME);
        for (String namespace : namespaceList) {
            Config config = ConfigService.getConfig(namespace);

            composite.addPropertySource(configPropertySourceFactory.getConfigPropertySource(namespace, config));
        }

        environment.getPropertySources().addFirst(composite);
    }

    private boolean hasInitialized(ConfigurableEnvironment environment) {
        return environment.getPropertySources().contains(PropertySourcesConstants.APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME);
    }

    /**
     * To fill system properties from environment config
     */
    void initializeSystemProperty(ConfigurableEnvironment environment) {
        for (String propertyName : APOLLO_SYSTEM_PROPERTIES) {
            fillSystemPropertyFromEnvironment(environment, propertyName);
        }
    }

    private void fillSystemPropertyFromEnvironment(ConfigurableEnvironment environment, String propertyName) {
        if (System.getProperty(propertyName) != null) {
            return;
        }

        String propertyValue = environment.getProperty(propertyName);

        if (Strings.isNullOrEmpty(propertyValue)) {
            return;
        }

        System.setProperty(propertyName, propertyValue);
    }

    /**
     * In order to load Apollo configurations as early as even before Spring loading logging system phase,
     * this EnvironmentPostProcessor can be called Just After ConfigFileApplicationListener has succeeded.
     * <p>
     * <br />
     * The processing sequence would be like this: <br />
     * Load Bootstrap properties and application properties -----> load Apollo configuration properties ----> Initialize Logging systems
     *
     * @param configurableEnvironment
     * @param springApplication
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment configurableEnvironment, SpringApplication springApplication) {

        // should always initialize system properties like app.id in the first place
        initializeSystemProperty(configurableEnvironment);

        Boolean eagerLoadEnabled = configurableEnvironment.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED, Boolean.class, false);

        //EnvironmentPostProcessor should not be triggered if you don't want Apollo Loading before Logging System Initialization
        if (!eagerLoadEnabled) {
            return;
        }

        Boolean bootstrapEnabled = configurableEnvironment.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED, Boolean.class, false);

        if (bootstrapEnabled) {
            initialize(configurableEnvironment);
        }

    }
}

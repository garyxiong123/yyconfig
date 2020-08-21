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

import com.yofish.yyconfig.client.component.annotation.ApolloAnnotationProcessor;
import com.yofish.yyconfig.client.component.annotation.ApolloJsonValueProcessor;
import com.yofish.yyconfig.client.component.annotation.SpringValueProcessor;
import com.yofish.yyconfig.client.component.spring.property.SpringValueDefinitionProcessor;
import com.yofish.yyconfig.client.component.annotation.BeanRegistrationUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;

/**
 * Apollo Property Sources processor for Spring XML Based Application
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigPropertySourcesProcessor extends PropertySourcesProcessor
    implements BeanDefinitionRegistryPostProcessor {

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    Map<String, Object> propertySourcesPlaceholderPropertyValues = new HashMap<>();
    // to make sure the default PropertySourcesPlaceholderConfigurer's priority is higher than PropertyPlaceholderConfigurer
    propertySourcesPlaceholderPropertyValues.put("order", 0);

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesPlaceholderConfigurer.class.getName(),
        PropertySourcesPlaceholderConfigurer.class, propertySourcesPlaceholderPropertyValues);
    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApolloAnnotationProcessor.class.getName(),
        ApolloAnnotationProcessor.class);
    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueProcessor.class.getName(), SpringValueProcessor.class);
    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApolloJsonValueProcessor.class.getName(),
        ApolloJsonValueProcessor.class);

    processSpringValueDefinition(registry);
  }

  /**
   * For Spring 3.x versions, the BeanDefinitionRegistryPostProcessor would not be
   * instantiated if it is added in postProcessBeanDefinitionRegistry phase, so we have to manually
   * call the postProcessBeanDefinitionRegistry method of SpringValueDefinitionProcessor here...
   */
  private void processSpringValueDefinition(BeanDefinitionRegistry registry) {
    SpringValueDefinitionProcessor springValueDefinitionProcessor = new SpringValueDefinitionProcessor();

    springValueDefinitionProcessor.postProcessBeanDefinitionRegistry(registry);
  }
}

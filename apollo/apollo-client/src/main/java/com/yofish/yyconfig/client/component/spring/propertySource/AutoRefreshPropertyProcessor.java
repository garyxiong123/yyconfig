package com.yofish.yyconfig.client.component.spring.propertySource;

import com.yofish.yyconfig.client.component.annotation.ApolloPropertyRefresh;
import com.yofish.yyconfig.client.component.exceptions.ApolloConfigException;
import com.yofish.yyconfig.client.domain.config.Config;
import com.yofish.yyconfig.client.lifecycle.preboot.ConfigService;
import com.yofish.yyconfig.client.pattern.listener.config.ConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author WangSongJun
 * @date 2020-11-20
 */
@Slf4j
@Configuration
public class AutoRefreshPropertyProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext context;

    private static final List<String> APOLLO_LISTENER_LIST = new ArrayList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (APOLLO_LISTENER_LIST.contains(beanName)) {
            return bean;
        }
        ApolloPropertyRefresh apolloPropertyRefresh = AnnotationUtils.findAnnotation(bean.getClass(), ApolloPropertyRefresh.class);
        ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(bean.getClass(), ConfigurationProperties.class);
        boolean assignableFrom = ApolloRefreshDisposableBean.class.isAssignableFrom(bean.getClass());
        if (apolloPropertyRefresh == null || configurationProperties == null || !assignableFrom) {
            return bean;
        }
        String[] namespaces = apolloPropertyRefresh.namespaces();
        if (namespaces.length == 0) {
            return bean;
        }
        String prefix = configurationProperties.prefix();
        ConfigChangeListener configChangeListener = changeEvent -> {
            Set<String> changedKeys = changeEvent.changedKeys();
            if (CollectionUtils.isEmpty(changedKeys)) {
                return;
            }
            if (StringUtils.hasText(prefix)) {
                changedKeys = changedKeys.stream()
                        .filter(changedKey -> changedKey.startsWith(prefix))
                        .collect(Collectors.toSet());
                if (CollectionUtils.isEmpty(changedKeys)) {
                    log.warn("{}的部分配置项{}被修改, 但不涉及'{}'", changeEvent.getNamespace(), changeEvent.changedKeys(), beanName);
                    return;
                }
            }
            ConfigurationPropertiesRebinder reBinder = context.getBean(ConfigurationPropertiesRebinder.class);
            reBinder.rebind(beanName);
            log.info("{}的部分配置项{}被修改, 以'{}.'开头的配置已自动更新到'{}'实例中", changeEvent.getNamespace(), changedKeys, prefix, beanName);
        };
        for (String namespace : namespaces) {
            Config config;
            try {
                config = ConfigService.getConfig(namespace);
            } catch (ApolloConfigException apolloConfigException) {
                config = null;
                log.warn("namespace: {} 不存在", namespace);
            }
            if (config != null) {
                config.addChangeListener(configChangeListener);
                log.info("实例{}已启动自动更新. 命名空间{}中以'{}.'开头的配置的变更会自动更新到'{}'实例中", beanName, namespace, prefix, beanName);
            }
        }
        APOLLO_LISTENER_LIST.add(beanName);
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}

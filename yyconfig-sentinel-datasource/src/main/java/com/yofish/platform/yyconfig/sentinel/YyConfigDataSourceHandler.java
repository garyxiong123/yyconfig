package com.yofish.platform.yyconfig.sentinel;

import com.alibaba.cloud.sentinel.datasource.config.AbstractDataSourceProperties;
import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * YyConfigDataSourceHandler
 *
 * @author WangSongJun
 * @date 2021-12-09
 */
public class YyConfigDataSourceHandler implements SmartInitializingSingleton {
    private static final Logger log = LoggerFactory.getLogger(YyConfigDataSourceHandler.class);
    private List<String> dataTypeList = Arrays.asList("json", "xml");
    private final String DATA_TYPE_FIELD = "dataType";
    private final String CUSTOM_DATA_TYPE = "custom";
    private final String CONVERTER_CLASS_FIELD = "converterClass";
    private final DefaultListableBeanFactory beanFactory;
    private final YyConfigProperties yyConfigProperties;
    private final Environment env;

    public YyConfigDataSourceHandler(DefaultListableBeanFactory beanFactory, YyConfigProperties yyConfigProperties, Environment env) {
        this.beanFactory = beanFactory;
        this.yyConfigProperties = yyConfigProperties;
        this.env = env;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.yyConfigProperties.getDatasource().forEach((dataSourceName, dataSourceProperties) -> {
            try {
                dataSourceProperties.setEnv(this.env);
                dataSourceProperties.preCheck(dataSourceName);
                this.registerBean(dataSourceProperties, dataSourceName + "-sentinel-yyconfig-datasource");
            } catch (Exception var5) {
                log.error("[Sentinel Starter] DataSource " + dataSourceName + " build error: " + var5.getMessage(), var5);
            }

        });
    }

    private void registerBean(final AbstractDataSourceProperties dataSourceProperties, String dataSourceName) {
        Map<String, Object> propertyMap = (Map)Arrays.stream(dataSourceProperties.getClass().getDeclaredFields()).collect(HashMap::new, (m, v) -> {
            try {
                v.setAccessible(true);
                m.put(v.getName(), v.get(dataSourceProperties));
            } catch (IllegalAccessException var5) {
                log.error("[Sentinel Starter] DataSource " + dataSourceName + " field: " + v.getName() + " invoke error");
                throw new RuntimeException("[Sentinel Starter] DataSource " + dataSourceName + " field: " + v.getName() + " invoke error", var5);
            }
        }, HashMap::putAll);
        propertyMap.put("converterClass", dataSourceProperties.getConverterClass());
        propertyMap.put("dataType", dataSourceProperties.getDataType());
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(dataSourceProperties.getFactoryBeanName());
        propertyMap.forEach((propertyName, propertyValue) -> {
            Field field = ReflectionUtils.findField(dataSourceProperties.getClass(), propertyName);
            if (null != field) {
                if ("dataType".equals(propertyName)) {
                    String dataType = StringUtils.trimAllWhitespace(propertyValue.toString());
                    if ("custom".equals(dataType)) {
                        try {
                            if (StringUtils.isEmpty(dataSourceProperties.getConverterClass())) {
                                throw new RuntimeException("[Sentinel Starter] DataSource " + dataSourceName + "dataType is custom, please set converter-class property");
                            }

                            String customConvertBeanName = "sentinel-" + dataSourceProperties.getConverterClass();
                            if (!this.beanFactory.containsBean(customConvertBeanName)) {
                                this.beanFactory.registerBeanDefinition(customConvertBeanName, BeanDefinitionBuilder.genericBeanDefinition(Class.forName(dataSourceProperties.getConverterClass())).getBeanDefinition());
                            }

                            builder.addPropertyReference("converter", customConvertBeanName);
                        } catch (ClassNotFoundException var9) {
                            log.error("[Sentinel Starter] DataSource " + dataSourceName + " handle " + dataSourceProperties.getClass().getSimpleName() + " error, class name: " + dataSourceProperties.getConverterClass());
                            throw new RuntimeException("[Sentinel Starter] DataSource " + dataSourceName + " handle " + dataSourceProperties.getClass().getSimpleName() + " error, class name: " + dataSourceProperties.getConverterClass(), var9);
                        }
                    } else {
                        if (!this.dataTypeList.contains(StringUtils.trimAllWhitespace(propertyValue.toString()))) {
                            throw new RuntimeException("[Sentinel Starter] DataSource " + dataSourceName + " dataType: " + propertyValue + " is not support now. please using these types: " + this.dataTypeList.toString());
                        }

                        builder.addPropertyReference("converter", "sentinel-" + propertyValue.toString() + "-" + dataSourceProperties.getRuleType().getName() + "-converter");
                    }
                } else {
                    if ("converterClass".equals(propertyName)) {
                        return;
                    }

                    Optional.ofNullable(propertyValue).ifPresent((v) -> {
                        builder.addPropertyValue(propertyName, v);
                    });
                }

            }
        });
        this.beanFactory.registerBeanDefinition(dataSourceName, builder.getBeanDefinition());
        AbstractDataSource newDataSource = (AbstractDataSource)this.beanFactory.getBean(dataSourceName);
        dataSourceProperties.postRegister(newDataSource);
    }

}

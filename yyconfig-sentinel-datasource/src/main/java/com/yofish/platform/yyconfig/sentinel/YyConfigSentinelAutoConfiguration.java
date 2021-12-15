package com.yofish.platform.yyconfig.sentinel;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * YyConfigSentinelAutoConfiguration
 *
 * @author WangSongJun
 * @date 2021-12-09
 */
@Configuration
@ConditionalOnProperty(
        name = {"spring.cloud.sentinel.enabled"},
        matchIfMissing = true
)
@EnableConfigurationProperties({YyConfigProperties.class})
public class YyConfigSentinelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public YyConfigDataSourceHandler yyConfigDataSourceHandler(DefaultListableBeanFactory beanFactory, YyConfigProperties yyConfigProperties, Environment env) {
        return new YyConfigDataSourceHandler(beanFactory, yyConfigProperties, env);
    }
}

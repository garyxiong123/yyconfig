/*
 *    Copyright 2018-2019 the original author or authors.
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
package com.yofish.gary.component.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static java.util.Collections.singletonList;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.web.cors.CorsConfiguration.ALL;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro 跨域bean配置
 */
@Configuration
public class ShiroCorsConfiguration {

    @Bean
    public FilterRegistrationBean crosFilterRegistrationBean() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", getCorsConfiguration());
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new CorsFilter(urlBasedCorsConfigurationSource));
        filterRegistrationBean.setOrder(HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    /**
     * 获取cors配置
     *
     * @return
     */
    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(singletonList(ALL));
        corsConfiguration.setAllowedHeaders(singletonList(ALL));
        corsConfiguration.setAllowedMethods(singletonList(ALL));
        corsConfiguration.addExposedHeader("Authorization");
        return corsConfiguration;
    }
}


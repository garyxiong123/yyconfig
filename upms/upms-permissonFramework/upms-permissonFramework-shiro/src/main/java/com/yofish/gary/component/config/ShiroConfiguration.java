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

import com.yofish.gary.component.filter.ShiroUrlPathMatchingFilter;
import com.yofish.gary.api.properties.ShiroProperties;
import com.yofish.gary.component.realm.ShiroAuthRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.split;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro bean配置
 */
@Configuration
public class ShiroConfiguration {

    @Autowired
    private ShiroProperties shiroProperties;


    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager());
        shiroFilterFactoryBean.setLoginUrl(shiroProperties.getLogin());
        shiroFilterFactoryBean.setUnauthorizedUrl(shiroProperties.getUnauthorized());

        shiroFilterFactoryBean.setFilters(getFilterMap());
        shiroFilterFactoryBean.setFilterChainDefinitionMap(getFilterChainDefinitionMap());
        return shiroFilterFactoryBean;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(defaultIfBlank(shiroProperties.getHashAlgorithmName(), "sha-256)"));
        return hashedCredentialsMatcher;
    }

    @Bean
    public ShiroAuthRealm shiroAuthRealm() {
        ShiroAuthRealm shiroAuthRealm = new ShiroAuthRealm();
        shiroAuthRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return shiroAuthRealm;
    }

    @Bean
    public SessionDAO sessionDAO() {
        return new MemorySessionDAO();
    }

    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionDAO(sessionDAO());
        defaultWebSessionManager.setGlobalSessionTimeout(86400000L);
        defaultWebSessionManager.getSessionIdCookie().setMaxAge(-1);

        return defaultWebSessionManager;
    }


    @Bean(name = "securityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(shiroAuthRealm());
        defaultWebSecurityManager.setSessionManager(sessionManager());
        defaultWebSecurityManager.setRememberMeManager(null);
        SecurityUtils.setSecurityManager(defaultWebSecurityManager);
        return defaultWebSecurityManager;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public ShiroUrlPathMatchingFilter shiroUrlPathMatchingFilter() {
        return new ShiroUrlPathMatchingFilter();
    }

    /**
     * 组装过滤器FilterMap
     *
     * @return
     */
    private Map<String, Filter> getFilterMap() {
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        String requestUrlFilterKey = defaultIfBlank(shiroProperties.getUrlFilterKey(), "url");
        filterMap.put(requestUrlFilterKey, shiroUrlPathMatchingFilter());
        return filterMap;
    }

    /**
     * 组装FilterChainDefinitionMap(包括:anon,user,authc和自定义的url)
     *
     * @return
     */
    private Map<String, String> getFilterChainDefinitionMap() {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        String[] noneUrlKeyArray = split(shiroProperties.getNoneUrlKeys(), ",");
        for (String noneUrlKey : noneUrlKeyArray) {
            filterChainDefinitionMap.put(noneUrlKey, "anon");
        }

        String[] authcUrlKeyArray = split(shiroProperties.getAuthcUrlKeys(), ",");
        if (isEmpty(authcUrlKeyArray)) {return filterChainDefinitionMap;}
        for (String authcUrlKey : authcUrlKeyArray) {
            filterChainDefinitionMap.put(authcUrlKey, "user,url");
        }
        return filterChainDefinitionMap;
    }

}

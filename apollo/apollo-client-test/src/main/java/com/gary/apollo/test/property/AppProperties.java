package com.gary.apollo.test.property;

import com.gary.apollo.test.model.Permission;
import com.yofish.yyconfig.client.component.annotation.ApolloPropertyRefresh;
import com.yofish.yyconfig.client.component.spring.propertySource.ApolloRefreshDisposableBean;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * 在 ApolloPropertyRefresh 中添加需要监听的namespace
 * 添加实现 ApolloRefreshDisposableBean
 *
 * @author WangSongJun
 * @date 2020-11-20
 */
@Data
@Component
@ApolloPropertyRefresh(namespaces = "application")
@ConfigurationProperties(prefix = "app")
public class AppProperties implements ApolloRefreshDisposableBean {
    private String info;
    private Permission permission = new Permission();
}

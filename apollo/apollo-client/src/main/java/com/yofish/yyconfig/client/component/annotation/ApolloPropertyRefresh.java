package com.yofish.yyconfig.client.component.annotation;

import java.lang.annotation.*;

/**
 * @author Ping
 * @date 2018/11/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ApolloPropertyRefresh {

    /**
     * 监听的namespace 必填项
     *
     * @return namespaces
     */
    String[] namespaces();
}

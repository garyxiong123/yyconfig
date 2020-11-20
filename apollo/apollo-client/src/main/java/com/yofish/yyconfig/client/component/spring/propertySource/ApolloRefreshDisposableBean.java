package com.yofish.yyconfig.client.component.spring.propertySource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author Ping
 * @date 2018/11/29
 */
public interface ApolloRefreshDisposableBean extends DisposableBean {

    /**
     * Invoked by a BeanFactory on destruction of a singleton.
     *
     * @throws Exception in case of shutdown errors.
     *                   Exceptions will get logged but not rethrown to allow
     *                   other beans to release their resources too.
     */
    @Override
    default void destroy() throws Exception {
        BeanUtils.copyProperties(BeanUtils.instantiateClass(this.getClass()), this);
    }
}

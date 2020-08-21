package com.yofish.yyconfig.client.lifecycle.preboot.inject.beans;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.yofish.yyconfig.client.component.spring.property.PlaceholderHelper;
import com.yofish.yyconfig.client.component.spring.property.SpringValueRegistry;
import com.yofish.yyconfig.client.component.spring.propertySource.ConfigPropertySourceFactory;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/8/19 下午4:46
 */
public class SpringModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlaceholderHelper.class).in(Singleton.class);
        bind(ConfigPropertySourceFactory.class).in(Singleton.class);
        bind(SpringValueRegistry.class).in(Singleton.class);
    }
}
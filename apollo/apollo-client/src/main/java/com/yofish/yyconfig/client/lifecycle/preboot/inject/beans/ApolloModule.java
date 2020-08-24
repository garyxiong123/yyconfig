package com.yofish.yyconfig.client.lifecycle.preboot.inject.beans;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.yofish.yyconfig.client.component.util.http.HttpUtil;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ClientConfig;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ConfigManager;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ConfigServiceLocator;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.DefaultConfigManager;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.factory.ConfigFactory;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.factory.ConfigFactoryManager;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.factory.DefaultConfigFactory;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.factory.DefaultConfigFactoryManager;
import com.yofish.yyconfig.client.timer.VersionMonitor4Namespace;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/8/19 下午4:45
 */
public class ApolloModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ConfigManager.class).to(DefaultConfigManager.class).in(Singleton.class);
        bind(ConfigFactoryManager.class).to(DefaultConfigFactoryManager.class).in(Singleton.class);
        bind(ConfigFactory.class).to(DefaultConfigFactory.class).in(Singleton.class);
        bind(ClientConfig.class).in(Singleton.class);
        bind(HttpUtil.class).in(Singleton.class);
        bind(ConfigServiceLocator.class).in(Singleton.class);
        bind(VersionMonitor4Namespace.class).in(Singleton.class);
    }
}
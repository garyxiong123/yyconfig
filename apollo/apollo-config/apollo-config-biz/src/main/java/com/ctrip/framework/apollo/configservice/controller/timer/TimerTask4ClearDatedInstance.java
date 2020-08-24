///*
// *    Copyright 2019-2020 the original author or authors.
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//package com.ctrip.framework.apollo.configservice.controller.timer;
//
//import com.ctrip.framework.apollo.configservice.domain.RegistryCenter;
//import com.ctrip.framework.apollo.configservice.wrapper.ClientConnection;
//import com.google.common.base.Joiner;
//import com.google.common.collect.Multimap;
//import com.yofish.apollo.domain.Instance;
//import com.yofish.apollo.domain.InstanceConfig;
//import com.yofish.apollo.repository.AppNamespaceRepository;
//import com.yofish.apollo.repository.InstanceConfigRepository;
//import com.yofish.apollo.repository.InstanceRepository;
//import com.yofish.apollo.service.PortalConfig;
//import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
//import com.yofish.yyconfig.common.framework.apollo.core.utils.ApolloThreadFactory;
//import lombok.Getter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
///**
// * 定时任务 清除过期的实例
// */
//@Getter
//@Component
//public class TimerTask4ClearDatedInstance implements InitializingBean {
//    private static final Logger logger = LoggerFactory.getLogger(TimerTask4ClearDatedInstance.class);
//    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).skipNulls();
//
//    @Autowired
//    private AppNamespaceRepository appNamespaceRepository;
//
//    @Autowired
//    private PortalConfig portalConfig;
//
//    private int scanInterval;
//    private TimeUnit scanIntervalTimeUnit;
//    private int rebuildInterval = 30;
//    private TimeUnit rebuildIntervalTimeUnit = TimeUnit.SECONDS;
//    private ScheduledExecutorService scheduledExecutorService;
//    private long maxIdScanned;
//    @Autowired
//    private AppNamespaceCache appNamespaceCache;
//    @Autowired
//    private InstanceRepository instanceRepository;
//    @Autowired
//    private InstanceConfigRepository instanceConfigRepository;
//    @Autowired
//    private RegistryCenter registryCenter;
//
//
//    public TimerTask4ClearDatedInstance() {
//        initialize();
//    }
//
//    private void initialize() {
//        maxIdScanned = 0;
//        scheduledExecutorService = Executors.newScheduledThreadPool(1, ApolloThreadFactory.create("TimerTask4ClearDatedInstance", true));
//    }
//
//
//    @Override
//    public void afterPropertiesSet() {
//        scanIntervalTimeUnit = TimeUnit.SECONDS;
//        scanInterval = 30;
//
//
//        //2：同步 修改和删除
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            clearInstanceOutOfDate();
//        }, rebuildInterval, 10 * rebuildInterval, rebuildIntervalTimeUnit);
//
//    }
//
//    private void clearInstanceOutOfDate() {
//    }
//
//}

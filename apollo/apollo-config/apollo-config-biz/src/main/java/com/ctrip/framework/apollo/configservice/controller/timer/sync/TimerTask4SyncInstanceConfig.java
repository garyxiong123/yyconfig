/*
 *    Copyright 2019-2020 the original author or authors.
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
package com.ctrip.framework.apollo.configservice.controller.timer.sync;

import com.ctrip.framework.apollo.configservice.domain.InstanceConfigRefresh;
import com.ctrip.framework.apollo.configservice.cache.InstanceConfigCache;
import com.google.common.collect.Queues;
import com.yofish.yyconfig.common.common.NamespaceBo;
import com.yofish.yyconfig.common.framework.apollo.core.utils.ApolloThreadFactory;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实例配置心跳池，放入队列
 */
@Component
public class TimerTask4SyncInstanceConfig implements InitializingBean {
    private static final int INSTANCE_CONFIG_AUDIT_MAX_SIZE = 10000;


    private final ExecutorService auditExecutorService;
    private final AtomicBoolean auditStopped;
    private BlockingQueue<InstanceConfigRefresh> audits = Queues.newLinkedBlockingQueue(INSTANCE_CONFIG_AUDIT_MAX_SIZE);

    @Autowired
    private InstanceConfigCache instanceConfigCache;


    public TimerTask4SyncInstanceConfig() {
        auditExecutorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create("HeartBeatPool", true));
        auditStopped = new AtomicBoolean(false);

    }

    public boolean offerHeartBeat(NamespaceBo namespaceBo, String ip, String releaseKey) {
        return this.audits.offer(new InstanceConfigRefresh(namespaceBo, ip, releaseKey));
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        auditExecutorService.submit(() -> {
            while (!auditStopped.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    InstanceConfigRefresh instanceConfigRefresh = audits.poll();
                    if (instanceConfigRefresh == null) {
                        TimeUnit.SECONDS.sleep(1);
                        continue;
                    }
                    instanceConfigCache.doInstanceRefresh(instanceConfigRefresh);
                } catch (Throwable ex) {
                    Tracer.logError(ex);
                }
            }
        });
    }


}

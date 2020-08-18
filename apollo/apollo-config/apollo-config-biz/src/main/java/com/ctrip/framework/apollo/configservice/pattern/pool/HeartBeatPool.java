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
package com.ctrip.framework.apollo.configservice.pattern.pool;

import com.ctrip.framework.apollo.configservice.config.InstanceConfigRefreshModel;
import com.google.common.collect.Queues;
import common.NamespaceBo;
import framework.apollo.core.utils.ApolloThreadFactory;
import framework.apollo.tracer.Tracer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实例配置心跳池，放入队列
 */
@Service
public class HeartBeatPool implements InitializingBean {
    private static final int INSTANCE_CONFIG_AUDIT_MAX_SIZE = 10000;


    private final ExecutorService auditExecutorService;
    private final AtomicBoolean auditStopped;
    private BlockingQueue<InstanceConfigRefreshModel> audits = Queues.newLinkedBlockingQueue(INSTANCE_CONFIG_AUDIT_MAX_SIZE);

    @Autowired
    private HeartBeatExecutor heartBeatExecutor;


    public HeartBeatPool() {
        auditExecutorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create("InstanceConfigAuditUtil", true));
        auditStopped = new AtomicBoolean(false);

    }

    public boolean offerHeartBeat(NamespaceBo namespaceBo, String ip, String releaseKey) {
        return this.audits.offer(new InstanceConfigRefreshModel(namespaceBo, ip, releaseKey));
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        auditExecutorService.submit(() -> {
            while (!auditStopped.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    InstanceConfigRefreshModel instanceConfigRefreshModel = audits.poll();
                    if (instanceConfigRefreshModel == null) {
                        TimeUnit.SECONDS.sleep(1);
                        continue;
                    }
                    heartBeatExecutor.doHeartBeat(instanceConfigRefreshModel);
                } catch (Throwable ex) {
                    Tracer.logError(ex);
                }
            }
        });
    }


}

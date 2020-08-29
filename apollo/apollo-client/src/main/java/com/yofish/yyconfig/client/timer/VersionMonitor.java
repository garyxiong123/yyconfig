package com.yofish.yyconfig.client.timer;

import com.google.common.util.concurrent.RateLimiter;
import com.yofish.yyconfig.client.component.exceptions.ApolloConfigException;
import com.yofish.yyconfig.client.component.util.ExceptionUtil;
import com.yofish.yyconfig.client.domain.Client;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.ApolloInjector;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ClientConfig;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ConfigServiceLocator;
import com.yofish.yyconfig.client.repository.RemoteConfigRepository;
import com.yofish.yyconfig.common.framework.apollo.core.schedule.ExponentialSchedulePolicy;
import com.yofish.yyconfig.common.framework.apollo.core.schedule.SchedulePolicy;
import com.yofish.yyconfig.common.framework.apollo.core.utils.ApolloThreadFactory;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定时任务： 客户端 建立config端的长连接:  b
 */
@Slf4j
public class VersionMonitor {
    private static final Logger logger = LoggerFactory.getLogger(VersionMonitor.class);
    //90 seconds, should be longer than server side's long polling timeout, which is now 60 seconds
    private SchedulePolicy m_longPollFailSchedulePolicyInSecond;

    private final ExecutorService m_longPollingService;
    private final AtomicBoolean m_longPollingStopped;

    private RateLimiter m_longPollRateLimiter;
    private final AtomicBoolean monitor_Started;

    private ClientConfig clientConfig;

    private ConfigServiceLocator m_serviceLocator;
    private Client client;

    /**
     * Constructor.
     */
    public VersionMonitor() {
        m_longPollingStopped = new AtomicBoolean(false);
        m_longPollingService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create("VersionMonitor4Namespace", true));
        monitor_Started = new AtomicBoolean(false);
        m_longPollFailSchedulePolicyInSecond = new ExponentialSchedulePolicy(1, 120); //in second

        clientConfig = ApolloInjector.getInstance(ClientConfig.class);

        m_serviceLocator = ApolloInjector.getInstance(ConfigServiceLocator.class);
        m_longPollRateLimiter = RateLimiter.create(clientConfig.getLongPollQPS());
        client = clientConfig.getOrCreateClient();

    }

    /**
     * 添加到版本变更控制
     *
     * @param namespace
     * @param remoteConfigRepository
     * @return
     */
    public boolean add2VersionMonitor(String namespace, RemoteConfigRepository remoteConfigRepository) {
        boolean added = client.addConfigRepository(namespace, remoteConfigRepository);

        if (notStart()) {
            startVersionMonitor();
        }
        return added;
    }

    /**
     * 监控还未启动
     *
     * @return
     */
    private boolean notStart() {
        return !monitor_Started.get() && monitor_Started.compareAndSet(false, true);
    }

    private void startVersionMonitor() {
        try {
            final long longPollingInitialDelayInMills = clientConfig.getLongPollingInitialDelayInMills();
            m_longPollingService.submit(() -> {
                if (longPollingInitialDelayInMills > 0) {
                    try {
                        logger.debug("Long polling will start in {} ms.", longPollingInitialDelayInMills);TimeUnit.MILLISECONDS.sleep(longPollingInitialDelayInMills);
                    } catch (InterruptedException e) {
                    }
                }
                while (!m_longPollingStopped.get() && !Thread.currentThread().isInterrupted()) {
                    if (!m_longPollRateLimiter.tryAcquire(5, TimeUnit.SECONDS)) {
                        //wait at most 5 seconds
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                        }
                    }

                    compareVersionAndSync();
                }
            });
        } catch (Throwable ex) {
            monitor_Started.set(false);
            ApolloConfigException exception = new ApolloConfigException("Schedule long polling refresh failed", ex);
            Tracer.logError(exception);
            logger.warn(ExceptionUtil.getDetailMessage(exception));
        }
    }


    public void compareVersionAndSync() {
        try {
            client.versionCompareAndSync();
        } catch (Throwable ex) {
            Tracer.logEvent("ApolloConfigException", ExceptionUtil.getDetailMessage(ex));
            long sleepTimeInSecond = m_longPollFailSchedulePolicyInSecond.fail();
            logger.warn("Long polling failed, will retry in {} seconds. appCode: {}, appEnvCluster: {}, namespaces: {}, long polling url: {}, reason: {}", sleepTimeInSecond, client.getAppId(), client.getCluster(), client.assembleNamespaces(), client.getUrl(), ExceptionUtil.getDetailMessage(ex));
            try {
                TimeUnit.SECONDS.sleep(sleepTimeInSecond);
            } catch (InterruptedException ie) {
            }
        }
    }


    void stopLongPollingRefresh() {
        this.m_longPollingStopped.compareAndSet(false, true);
    }


}

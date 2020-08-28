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

import com.ctrip.framework.apollo.configservice.cache.ReleaseMessageCache;
import com.yofish.apollo.service.PortalConfig;
import com.yofish.yyconfig.common.framework.apollo.core.utils.ApolloThreadFactory;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Data
@Component
public class TimerTask4SyncReleaseMessage2Cache implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(TimerTask4SyncReleaseMessage2Cache.class);


    @Autowired
    private PortalConfig bizConfig;

    private int scanInterval = 3;
    private TimeUnit scanIntervalTimeUnit;


    private AtomicBoolean doScan;
    private ExecutorService executorService;
    @Autowired
    private ReleaseMessageCache releaseMessageCache;

    public TimerTask4SyncReleaseMessage2Cache() {
        initialize();
    }

    private void initialize() {

        doScan = new AtomicBoolean(true);
        executorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create("TimerTask4SyncReleaseMessage2Cache", true));
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        populateDataBaseInterval();
        //block the startup process until load finished
        //this should happen before ReleaseMessageScanner due to autowire
        releaseMessageCache.loadNewReleaseMessages();

        executorService.submit(() -> {
            while (doScan.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    releaseMessageCache.loadNewReleaseMessages();
                } catch (Throwable ex) {
                    logger.error("Scan new release messages failed", ex);
                } finally {
                }
                try {
                    scanIntervalTimeUnit.sleep(5);
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        });
    }


    private void populateDataBaseInterval() {
        scanInterval = bizConfig.releaseMessageCacheScanInterval();
        scanIntervalTimeUnit = bizConfig.releaseMessageCacheScanIntervalTimeUnit();
    }

    //only for test use
    private void reset() throws Exception {
        executorService.shutdownNow();
        initialize();
        afterPropertiesSet();
    }

    public void stopScan() {
        setDoScan(new AtomicBoolean(false));
    }
}

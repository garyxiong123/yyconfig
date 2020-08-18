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
package com.ctrip.framework.apollo.configservice.controller.timer;

import com.google.common.base.Joiner;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.yofish.apollo.service.PortalConfig;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.utils.ApolloThreadFactory;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 存在的目的就是缓存同步
 */
@Getter
@Component
public class TimerTask4SyncAppNamespaceCache implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(TimerTask4SyncAppNamespaceCache.class);
    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).skipNulls();

    @Autowired
    private AppNamespaceRepository appNamespaceRepository;

    @Autowired
    private PortalConfig portalConfig;

    private int scanInterval;
    private TimeUnit scanIntervalTimeUnit;
    private int rebuildInterval = 30;
    private TimeUnit rebuildIntervalTimeUnit = TimeUnit.SECONDS;
    private ScheduledExecutorService scheduledExecutorService;
    private long maxIdScanned;
    @Autowired
    private AppNamespaceCache appNamespaceCache;


    public TimerTask4SyncAppNamespaceCache() {
        initialize();
    }

    private void initialize() {
        maxIdScanned = 0;
//        appNamespaceCache = new AppNamespaceCache();
        scheduledExecutorService = Executors.newScheduledThreadPool(1, ApolloThreadFactory.create("AppNamespaceServiceWithCache", true));
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        populateDataBaseInterval();
        scanNewAppNamespaces(); //block the startup process until load finished
        scanIntervalTimeUnit = TimeUnit.SECONDS;
        scanInterval = 30;

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            appNamespaceCache.updateAndDeleteCache();
        }, rebuildInterval, rebuildInterval, rebuildIntervalTimeUnit);

        scheduledExecutorService.scheduleWithFixedDelay(this::scanNewAppNamespaces, scanInterval,
                scanInterval, scanIntervalTimeUnit);
    }

    private void scanNewAppNamespaces() {
        this.loadNewAppNamespacesAndMergeCache();
    }

    //for those new app namespaces
    private void loadNewAppNamespacesAndMergeCache() {
        boolean hasMore = true;
        while (hasMore && !Thread.currentThread().isInterrupted()) {
            //current batch is 500
            if (hasNewAppNamespace()) {
                List<AppNamespace> newAppNamespaces = appNamespaceRepository.findFirst500ByIdGreaterThanOrderByIdAsc(maxIdScanned);

                appNamespaceCache.mergeCacheAppNamespaces(newAppNamespaces);

                hasMore = updateMaxIdScannedAndCalcHasMore(newAppNamespaces);

                logger.info("Loaded {} new app namespaces with startId {}", maxIdScanned, maxIdScanned);
            } else {
                hasMore = false;
            }

        }
    }

    private boolean updateMaxIdScannedAndCalcHasMore(List<AppNamespace> newAppNamespaces) {
        int scannedNum = newAppNamespaces.size();
        maxIdScanned = newAppNamespaces.get(scannedNum - 1).getId();
        boolean hasMore = scannedNum == 500;
        return hasMore;
    }

    private boolean hasNewAppNamespace() {
        List<AppNamespace> appNamespaces = appNamespaceRepository.findFirst500ByIdGreaterThanOrderByIdAsc(maxIdScanned);
        return !CollectionUtils.isEmpty(appNamespaces);
    }


    /**
     * 初始化基础配置（从数据库）
     */
    private void populateDataBaseInterval() {
        scanInterval = portalConfig.appNamespaceCacheScanInterval();
        scanIntervalTimeUnit = portalConfig.appNamespaceCacheScanIntervalTimeUnit();
        rebuildInterval = portalConfig.appNamespaceCacheRebuildInterval();
        rebuildIntervalTimeUnit = portalConfig.appNamespaceCacheRebuildIntervalTimeUnit();
    }

    //only for test use
    private void reset() throws Exception {
        scheduledExecutorService.shutdownNow();
        initialize();
        afterPropertiesSet();
    }
}

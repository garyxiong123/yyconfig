package com.ctrip.framework.apollo.configservice.controller;

import com.ctrip.framework.apollo.configservice.wrapper.ClientConnection;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.yofish.apollo.service.PortalConfig;
import framework.apollo.core.dto.NamespaceChangeNotification;
import framework.apollo.core.utils.ApolloThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 注册中心
 * @Date: 2020/8/19 上午9:30
 */
@Slf4j
@Component
public class RegistryCenter {
    /**
     * 核心数据结构： key 是什么
     */
    public final Multimap<String, ClientConnection> clientWatchedKeyAndDeferredResultWrapperMap = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    private final ExecutorService largeNotificationBatchExecutorService;

    @Autowired
    private PortalConfig bizConfig;

    public RegistryCenter() {
        largeNotificationBatchExecutorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create
                ("NotificationControllerV2", true));
    }

    public void onReceiveReleaseMessage(NamespaceChangeNotification configNotification, String key) {
        //create a new list to avoid ConcurrentModificationException
        List<ClientConnection> clientConnections = Lists.newArrayList(clientWatchedKeyAndDeferredResultWrapperMap.get(key));

        if (tooManyClientsConnection(clientConnections)) {
            doAsyncNotification(key, clientConnections, configNotification);
            return;
        }

        doSyncNotification(key, clientConnections, configNotification);
    }


    /**
     * 异步通知写到内存， 同时由内存发给client
     *
     * @param content
     * @param clientConnections
     * @param configNotification
     */
    private void doAsyncNotification(String content, List<ClientConnection> clientConnections, NamespaceChangeNotification configNotification) {
        largeNotificationBatchExecutorService.submit(() -> {
            log.debug("Async notify {} clients for key {} with batch {}", clientConnections.size(), content, bizConfig.releaseMessageNotificationBatch());

            for (int index = 0; index < clientConnections.size(); index++) {
                if (index > 0 && index % bizConfig.releaseMessageNotificationBatch() == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(bizConfig.releaseMessageNotificationBatchIntervalInMilli());
                    } catch (InterruptedException e) {
                        //ignore
                    }
                }
                log.debug("Async notify {}", clientConnections.get(index));
                clientConnections.get(index).setResponse(configNotification);
            }
        });
    }


    private boolean tooManyClientsConnection(List<ClientConnection> results) {
        return results.size() > bizConfig.releaseMessageNotificationBatch();
    }


    private void doSyncNotification(String content, List<ClientConnection> clientConnections, NamespaceChangeNotification configNotification) {
        log.debug("Notify {} clients for key {}", clientConnections.size(), content);

        for (ClientConnection result : clientConnections) {
            result.setResponse(configNotification);
        }
        log.debug("Notification completed");
    }


    public void remove(String clientWatchedKey, ClientConnection clientConnection) {
        clientWatchedKeyAndDeferredResultWrapperMap.remove(clientWatchedKey, clientConnection);

    }

    public void put(String clientWatchedKey, ClientConnection clientConnection) {
        clientWatchedKeyAndDeferredResultWrapperMap.put(clientWatchedKey, clientConnection);

    }


}
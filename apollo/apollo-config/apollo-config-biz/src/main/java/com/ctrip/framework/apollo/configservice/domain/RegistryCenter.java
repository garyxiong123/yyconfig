package com.ctrip.framework.apollo.configservice.domain;

import com.ctrip.framework.apollo.configservice.wrapper.ClientConnection;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.yofish.apollo.service.PortalConfig;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import com.yofish.yyconfig.common.framework.apollo.core.utils.ApolloThreadFactory;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 注册中心
 * @Date: 2020/8/19 上午9:30
 */
@Data
@Slf4j
@Component
public class RegistryCenter {
    /**
     * 核心数据结构： key 是什么 LongNsName 命名空间名称
     */
    private final Multimap<String, ClientConnection> longNsNameAndConnectionMap = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    private final ExecutorService largeNotificationBatchExecutorService;

    @Autowired
    private PortalConfig bizConfig;

    public RegistryCenter() {
        largeNotificationBatchExecutorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create("RegistryCenter", true));
    }

    /**
     * 发布新的 版本变更通知
     *
     * @param newNsVersion
     * @param longNsName
     */
    public void publishNewNsVersion(NamespaceVersion newNsVersion, String longNsName) {
        //create a new list to avoid ConcurrentModificationException
        List<ClientConnection> clientConnections = Lists.newArrayList(longNsNameAndConnectionMap.get(longNsName));

        if (tooManyClientsConnection(clientConnections)) {
            doAsyncNotification(longNsName, clientConnections, newNsVersion);
            return;
        }

        doSyncNotification(longNsName, clientConnections, newNsVersion);
    }

    /**
     * 注册监听  命名空间 名称， 和相应的连接
     *
     * @param clientConnection
     * @param longNsNames
     */
    public void registerWatchedLongNsNames(ClientConnection clientConnection, Set<String> longNsNames) {
        //register all keys
        for (String longNsName : longNsNames) {
            this.put(longNsName, clientConnection);
        }

        logWatchedKeys(longNsNames, "Apollo.LongPoll.RegisteredKeys");
    }


    /**
     * 异步通知写到内存， 同时由内存发给client
     *
     * @param content
     * @param clientConnections
     * @param configNotification
     */
    private void doAsyncNotification(String content, List<ClientConnection> clientConnections, NamespaceVersion configNotification) {
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


    private void doSyncNotification(String content, List<ClientConnection> clientConnections, NamespaceVersion configNotification) {
        log.debug("Notify {} clients for key {}", clientConnections.size(), content);

        for (ClientConnection result : clientConnections) {
            result.setResponse(configNotification);
        }
        log.debug("Notification completed");
    }


    public void remove(String longNsName, ClientConnection clientConnection) {
        longNsNameAndConnectionMap.remove(longNsName, clientConnection);

    }

    public void put(String longNsName, ClientConnection clientConnection) {
        longNsNameAndConnectionMap.put(longNsName, clientConnection);

    }


    public static void logWatchedKeys(Set<String> longNsNames, String eventName) {
        for (String watchedKey : longNsNames) {
            Tracer.logEvent(eventName, watchedKey);
        }
    }

}

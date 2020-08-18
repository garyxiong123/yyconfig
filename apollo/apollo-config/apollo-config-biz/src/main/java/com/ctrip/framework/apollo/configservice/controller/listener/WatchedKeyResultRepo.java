package com.ctrip.framework.apollo.configservice.controller.listener;

import com.ctrip.framework.apollo.configservice.wrapper.DeferredResultWrapper;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.yofish.apollo.component.constant.PermissionType;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.pattern.listener.releasemessage.ReleaseMessageListener;
import com.yofish.apollo.service.PortalConfig;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.dto.ApolloConfigNotification;
import framework.apollo.core.utils.ApolloThreadFactory;
import framework.apollo.tracer.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 发布订阅的主要类
 * @Date: 2020/4/16 上午11:24
 */
@Slf4j
@Service
public class WatchedKeyResultRepo implements ReleaseMessageListener {

    private static final Splitter STRING_SPLITTER = Splitter.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).omitEmptyStrings();
    private final ExecutorService largeNotificationBatchExecutorService;

    public final Multimap<String, DeferredResultWrapper> clientWatchedKeyAndDeferredResultWrapperMap = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    @Autowired
    private PortalConfig bizConfig;

    public WatchedKeyResultRepo() {
        largeNotificationBatchExecutorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create
                ("NotificationControllerV2", true));
    }


    @Override
    public void onReceiveReleaseMessage(ReleaseMessage message, String channel) {
        String content = message.getMessage();
        handleMessageLog(message, channel, content);

//        if (isNotValidTopicOrNotInCache(message, channel)) {
//            return;
//        }
        ApolloConfigNotification configNotification = buildConfigNotification(message);

        //create a new list to avoid ConcurrentModificationException
        List<DeferredResultWrapper> deferredResultWrappers = Lists.newArrayList(clientWatchedKeyAndDeferredResultWrapperMap.get(content));
        //do async notification if too many clients
        if (tooManyClientsConnection(deferredResultWrappers)) {
            doAsyncNotification(content, deferredResultWrappers, configNotification);
            return;
        }

        doSyncNotification(content, deferredResultWrappers, configNotification);
    }


    private void handleMessageLog(ReleaseMessage message, String channel, String content) {
        log.info("message received - channel: {}, message: {}", channel, message);

        Tracer.logEvent("Apollo.LongPoll.Messages", content);
    }

    private void registerWatchedKeys(Set<String> watchedKeys) {

    }

    /**
     * 异步通知写到内存， 同时由内存发给client
     *
     * @param content
     * @param deferredResultWrappers
     * @param configNotification
     */
    private void doAsyncNotification(String content, List<DeferredResultWrapper> deferredResultWrappers, ApolloConfigNotification configNotification) {
        largeNotificationBatchExecutorService.submit(() -> {
            log.debug("Async notify {} clients for key {} with batch {}", deferredResultWrappers.size(), content, bizConfig.releaseMessageNotificationBatch());

            for (int index = 0; index < deferredResultWrappers.size(); index++) {
                if (index > 0 && index % bizConfig.releaseMessageNotificationBatch() == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(bizConfig.releaseMessageNotificationBatchIntervalInMilli());
                    } catch (InterruptedException e) {
                        //ignore
                    }
                }
                log.debug("Async notify {}", deferredResultWrappers.get(index));
                deferredResultWrappers.get(index).setDeferredResult(configNotification);
            }
        });
    }

    private boolean isNotValidTopicOrNotInCache(ReleaseMessage message, String channel) {
        String content = message.getMessage();
        if (!PermissionType.Topics.APOLLO_RELEASE_TOPIC.equals(channel) || isNullOrEmpty(content)) {
            return true;
        }
        String changedNamespace = retrieveNamespaceFromReleaseMessage.apply(content);

        if (isNullOrEmpty(changedNamespace)) {
            log.error("message format invalid - {}", content);
            return true;
        }

        if (!clientWatchedKeyAndDeferredResultWrapperMap.containsKey(content)) {
            return true;
        }
        return false;
    }

    private boolean tooManyClientsConnection(List<DeferredResultWrapper> results) {
        return results.size() > bizConfig.releaseMessageNotificationBatch();
    }


    private void doSyncNotification(String content, List<DeferredResultWrapper> deferredResultWrappers, ApolloConfigNotification configNotification) {
        log.debug("Notify {} clients for key {}", deferredResultWrappers.size(), content);

        for (DeferredResultWrapper result : deferredResultWrappers) {
            result.setDeferredResult(configNotification);
        }
        log.debug("Notification completed");
    }


    private ApolloConfigNotification buildConfigNotification(ReleaseMessage message) {
        String content = message.getMessage();
        String changedNamespace = retrieveNamespaceFromReleaseMessage.apply(content);
        ApolloConfigNotification configNotification = new ApolloConfigNotification(changedNamespace, message.getId());
        configNotification.addMessage(content, message.getId());
        return configNotification;
    }


    private static final Function<String, String> retrieveNamespaceFromReleaseMessage =
            releaseMessage -> {
                if (isNullOrEmpty(releaseMessage)) {
                    return null;
                }
                List<String> keys = STRING_SPLITTER.splitToList(releaseMessage);
                //message should be appCode+appEnvCluster+appNamespace
                if (keys.size() != 4) {
                    log.error("message format invalid - {}", releaseMessage);
                    return null;
                }
                return keys.get(3);
            };

    public void remove(String clientWatchedKey, DeferredResultWrapper deferredResultWrapper) {
        clientWatchedKeyAndDeferredResultWrapperMap.remove(clientWatchedKey, deferredResultWrapper);

    }

    public void put(String clientWatchedKey, DeferredResultWrapper deferredResultWrapper) {
        clientWatchedKeyAndDeferredResultWrapperMap.put(clientWatchedKey, deferredResultWrapper);

    }
}

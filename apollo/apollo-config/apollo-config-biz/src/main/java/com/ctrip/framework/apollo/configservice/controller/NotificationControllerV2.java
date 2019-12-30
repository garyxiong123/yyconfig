package com.ctrip.framework.apollo.configservice.controller;

import com.ctrip.framework.apollo.configservice.service.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.configservice.util.WatchKeysUtil;
import com.ctrip.framework.apollo.configservice.utils.EntityManagerUtil;
import com.ctrip.framework.apollo.configservice.wrapper.DeferredResultWrapper;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.message.ReleaseMessageListener;
import com.yofish.apollo.message.Topics;
import com.yofish.apollo.service.PortalConfig;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.dto.ApolloConfigNotification;
import framework.apollo.core.utils.ApolloThreadFactory;
import framework.apollo.tracer.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;
import static common.utils.YyStringUtils.notEqual;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RestController
@RequestMapping("/notifications/v2")
public class NotificationControllerV2 implements ReleaseMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(NotificationControllerV2.class);
    private final Multimap<String, DeferredResultWrapper> clientWatchedKeyAndDeferredResultWrapperMap = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private static final Splitter STRING_SPLITTER = Splitter.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).omitEmptyStrings();
    private static final Type notificationsTypeReference = new TypeToken<List<ApolloConfigNotification>>() {}.getType();

    private final ExecutorService largeNotificationBatchExecutorService;

    @Autowired
    private WatchKeysUtil watchKeysUtil;

    @Autowired
    private ReleaseMessageServiceWithCache releaseMessageService;

    @Autowired
    private EntityManagerUtil entityManagerUtil;

    @Autowired
    private NamespaceUtil namespaceUtil;

    @Autowired
    private Gson gson;

    @Autowired
    private PortalConfig bizConfig;

    public NotificationControllerV2() {
        largeNotificationBatchExecutorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create
                ("NotificationControllerV2", true));
    }

    @RequestMapping(method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> pollNotification4Client(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "cluster") String cluster,
            @RequestParam(value = "env") String env,
            @RequestParam(value = "notifications") String notificationsAsString,
            @RequestParam(value = "dataCenter", required = false) String dataCenter,
            @RequestParam(value = "ip", required = false) String clientIp) {


        DeferredResultWrapper deferredResultWrapper = new DeferredResultWrapper();
        Set<String> namespaces4ClientInterested = Sets.newHashSet();

        Map<String, ApolloConfigNotification> clientNotificationMap = createAndFilterClientNotification(notificationsAsString, appId);

        Map<String, Long> watchedNamespaceIdMap = buildWatchedNamespaceIdMapAndFillNormalizedNamespaceName2OriginalNamespaceNameMap(notificationsAsString, clientNotificationMap, deferredResultWrapper, namespaces4ClientInterested);

        Multimap<String, String> clientWatchedKeysMap = watchKeysUtil.assembleAllWatchKeys(appId, cluster, env, namespaces4ClientInterested, dataCenter);
        Set<String> clientWatchedKeys = Sets.newHashSet(clientWatchedKeysMap.values());

        /**
         * Manually close the entity manager.
         * Since for async request, Spring won't do so until the request is finished,
         * which is unacceptable since we are doing long polling - means the db connection would be hold
         * for a very long time
         */
        entityManagerUtil.closeEntityManager();

        List<ApolloConfigNotification> newInterestedServerNotification4CurrentRequest = getNewInterestedServerNotification4CurrentClientRequest(namespaces4ClientInterested, watchedNamespaceIdMap, clientWatchedKeysMap, clientWatchedKeys);

        if (isNotEmpty(newInterestedServerNotification4CurrentRequest)) {
            doSyncResponse(deferredResultWrapper, newInterestedServerNotification4CurrentRequest);
            // TODO 返回后断开连接，客户端继续连接
            return deferredResultWrapper.getDeferredResult();

        }

        doAsyncCallbackMethodAndWactchedKeyRegistry(appId, cluster, dataCenter, deferredResultWrapper, namespaces4ClientInterested, clientWatchedKeys);


        return deferredResultWrapper.getDeferredResult();
    }


    private void doSyncResponse(DeferredResultWrapper deferredResultWrapper, List<ApolloConfigNotification> newServerNotifications) {
        deferredResultWrapper.setResult(newServerNotifications);
    }

    private void doAsyncCallbackMethodAndWactchedKeyRegistry(@RequestParam(value = "appId") String appId, @RequestParam(value = "cluster") String cluster, @RequestParam(value = "dataCenter", required = false) String dataCenter, DeferredResultWrapper deferredResultWrapper, Set<String> namespaces, Set<String> clientWatchedKeys) {
        deferredResultWrapper.onTimeout(() -> logWatchedKeys(clientWatchedKeys, "Apollo.LongPoll.TimeOutKeys"));

        deferredResultWrapper.onCompletion(() -> {
            //unregister all keys
            for (String clientWatchedKey : clientWatchedKeys) {
                clientWatchedKeyAndDeferredResultWrapperMap.remove(clientWatchedKey, deferredResultWrapper);
            }
            logWatchedKeys(clientWatchedKeys, "Apollo.LongPoll.CompletedKeys");
        });

        registerWatchedKeys(deferredResultWrapper, clientWatchedKeys);

        logger.debug("Listening {} from appId: {}, cluster: {}, appNamespace: {}, datacenter: {}", clientWatchedKeys, appId, cluster, namespaces, dataCenter);
    }

    @Override
    public void handleReleaseMessage(ReleaseMessage message, String channel) {
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

    private void doSyncNotification(String content, List<DeferredResultWrapper> deferredResultWrappers, ApolloConfigNotification configNotification) {
        logger.debug("Notify {} clients for key {}", deferredResultWrappers.size(), content);

        for (DeferredResultWrapper result : deferredResultWrappers) {
            result.setDeferredResult(configNotification);
        }
        logger.debug("Notification completed");
    }

    private ApolloConfigNotification buildConfigNotification(ReleaseMessage message) {
        String content = message.getMessage();
        String changedNamespace = retrieveNamespaceFromReleaseMessage.apply(content);
        ApolloConfigNotification configNotification = new ApolloConfigNotification(changedNamespace, message.getId());
        configNotification.addMessage(content, message.getId());
        return configNotification;
    }

    private void doAsyncNotification(String content, List<DeferredResultWrapper> deferredResultWrappers, ApolloConfigNotification configNotification) {
        largeNotificationBatchExecutorService.submit(() -> {
            logger.debug("Async notify {} clients for key {} with batch {}", deferredResultWrappers.size(), content, bizConfig.releaseMessageNotificationBatch());

            for (int index = 0; index < deferredResultWrappers.size(); index++) {
                if (index > 0 && index % bizConfig.releaseMessageNotificationBatch() == 0) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(bizConfig.releaseMessageNotificationBatchIntervalInMilli());
                    } catch (InterruptedException e) {
                        //ignore
                    }
                }
                logger.debug("Async notify {}", deferredResultWrappers.get(index));
                deferredResultWrappers.get(index).setDeferredResult(configNotification);
            }
        });
    }

    private boolean isNotValidTopicOrNotInCache(ReleaseMessage message, String channel) {
        String content = message.getMessage();
        if (!Topics.APOLLO_RELEASE_TOPIC.equals(channel) || isNullOrEmpty(content)) {
            return true;
        }
        String changedNamespace = retrieveNamespaceFromReleaseMessage.apply(content);

        if (isNullOrEmpty(changedNamespace)) {
            logger.error("message format invalid - {}", content);
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

    private void registerWatchedKeys(DeferredResultWrapper deferredResultWrapper, Set<String> watchedKeys) {
        //register all keys
        for (String key : watchedKeys) {
            this.clientWatchedKeyAndDeferredResultWrapperMap.put(key, deferredResultWrapper);
        }

        logWatchedKeys(watchedKeys, "Apollo.LongPoll.RegisteredKeys");
    }

    private void registerWatchedKeys(Set<String> watchedKeys) {

    }

    private Map<String, Long> buildWatchedNamespaceIdMapAndFillNormalizedNamespaceName2OriginalNamespaceNameMap(String notificationsAsString, Map<String, ApolloConfigNotification> clientNotificationMap, DeferredResultWrapper deferredResultWrapper, Set<String> namespaces) {
        Map<String, Long> clientSideNotifications = Maps.newHashMap();
        for (Map.Entry<String, ApolloConfigNotification> notificationEntry : clientNotificationMap.entrySet()) {
            String normalizedNamespace = notificationEntry.getKey();
            ApolloConfigNotification notification = notificationEntry.getValue();
            namespaces.add(normalizedNamespace);
            clientSideNotifications.put(normalizedNamespace, notification.getNotificationId());
            if (notEqual(notification.getNamespaceName(), normalizedNamespace)) {
                String originalNamespaceName = notification.getNamespaceName();
                deferredResultWrapper.fillNormalizedNamespaceName2OriginalNamespaceNameMap(originalNamespaceName, normalizedNamespace);
            }
        }

        if (isEmpty(namespaces)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Invalid format of notifications: " + notificationsAsString);
        }
        return clientSideNotifications;
    }

    private Map<String, ApolloConfigNotification> createAndFilterClientNotification(String notificationsAsString, String appId) {
        List<ApolloConfigNotification> clientNotifications = gson.fromJson(notificationsAsString, notificationsTypeReference);
        if (isEmpty(clientNotifications)) {
//      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Invalid format of notifications: " + notificationsAsString);
        }
        Map<String, ApolloConfigNotification> filteredNotificationMap = filterNotifications(appId, clientNotifications);
        return filteredNotificationMap;
    }

    private Map<String, ApolloConfigNotification> filterNotifications(String appId, List<ApolloConfigNotification> notifications) {
        Map<String, ApolloConfigNotification> filteredNotifications = Maps.newHashMap();
        for (ApolloConfigNotification notification : notifications) {
            if (isNullOrEmpty(notification.getNamespaceName())) {
                continue;
            }
            //strip out .properties suffix
            String originalNamespace = namespaceUtil.filterNamespaceName(notification.getNamespaceName());
            notification.setNamespaceName(originalNamespace);
            //fix the character case issue, such as FX.apollo <-> fx.apollo
            String normalizedNamespace = namespaceUtil.normalizeNamespace(appId, originalNamespace);

            // in case client side appNamespace name has character case issue and has difference notification ids
            // such as FX.apollo = 1 but fx.apollo = 2, we should let FX.apollo have the chance to update its notification id
            // which means we should record FX.apollo = 1 here and ignore fx.apollo = 2
            if (filteredNotifications.containsKey(normalizedNamespace) &&
                    filteredNotifications.get(normalizedNamespace).getNotificationId() < notification.getNotificationId()) {
                continue;
            }

            filteredNotifications.put(normalizedNamespace, notification);
        }
        return filteredNotifications;
    }

    private List<ApolloConfigNotification> getNewInterestedServerNotification4CurrentClientRequest(Set<String> namespaces4ClientInterested,
                                                                                                   Map<String, Long> clientSideNotifications,
                                                                                                   Multimap<String, String> watchedKeysMap,
                                                                                                   Set<String> watchedKeys) {
        List<ReleaseMessage> latestReleaseMessages = releaseMessageService.findLatestReleaseMessagesGroupByMessages(watchedKeys);
        if (CollectionUtils.isEmpty(latestReleaseMessages)) {
            return null;
        }

        Map<String, Long> latestNotificationMap = Maps.newHashMap();
        latestReleaseMessages.forEach((releaseMessage) -> latestNotificationMap.put(releaseMessage.getMessage(), releaseMessage.getId()));

        List<ApolloConfigNotification> newNotifications = Lists.newArrayList();
        for (String namespace4ClientInterested : namespaces4ClientInterested) {
            long clientSideId = clientSideNotifications.get(namespace4ClientInterested);
            long latestId = ConfigConsts.NOTIFICATION_ID_PLACEHOLDER;
            Collection<String> namespaceWatchedKeys = watchedKeysMap.get(namespace4ClientInterested);
            for (String namespaceWatchedKey : namespaceWatchedKeys) {
                long namespaceNotificationId = latestNotificationMap.getOrDefault(namespaceWatchedKey, ConfigConsts.NOTIFICATION_ID_PLACEHOLDER);
                if (namespaceNotificationId > latestId) {
                    latestId = namespaceNotificationId;
                }
            }
            if (latestId > clientSideId) {
                ApolloConfigNotification notification = new ApolloConfigNotification(namespace4ClientInterested, latestId);
                namespaceWatchedKeys.stream().filter(latestNotificationMap::containsKey).forEach(namespaceWatchedKey ->
                        notification.addMessage(namespaceWatchedKey, latestNotificationMap.get(namespaceWatchedKey)));
                newNotifications.add(notification);
            }
        }
        return newNotifications;
    }


    private void handleMessageLog(ReleaseMessage message, String channel, String content) {
        logger.info("message received - channel: {}, message: {}", channel, message);

        Tracer.logEvent("Apollo.LongPoll.Messages", content);
    }

    private static final Function<String, String> retrieveNamespaceFromReleaseMessage =
            releaseMessage -> {
                if (isNullOrEmpty(releaseMessage)) {
                    return null;
                }
                List<String> keys = STRING_SPLITTER.splitToList(releaseMessage);
                //message should be appId+appEnvCluster+appNamespace
                if (keys.size() != 4) {
                    logger.error("message format invalid - {}", releaseMessage);
                    return null;
                }
                return keys.get(3);
            };

    private void logWatchedKeys(Set<String> watchedKeys, String eventName) {
        for (String watchedKey : watchedKeys) {
//            Tracer.logEvent(eventName, watchedKey);
        }
    }
}


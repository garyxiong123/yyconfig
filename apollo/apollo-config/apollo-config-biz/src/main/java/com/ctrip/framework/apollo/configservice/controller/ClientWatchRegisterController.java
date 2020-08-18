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
package com.ctrip.framework.apollo.configservice.controller;

import com.ctrip.framework.apollo.configservice.controller.listener.WatchedKeyResultRepo;
import com.ctrip.framework.apollo.configservice.repo.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.configservice.util.WatchKeysUtil;
import com.ctrip.framework.apollo.configservice.util.EntityManagerUtil;
import com.ctrip.framework.apollo.configservice.wrapper.DeferredResultWrapper;
import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yofish.apollo.domain.ReleaseMessage;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.dto.ApolloConfigNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.lang.reflect.Type;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static common.utils.YyStringUtils.notEqual;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Controller
@RequestMapping("/notifications/v2")
@Slf4j
public class ClientWatchRegisterController {

    private static final Type notificationsTypeReference = new TypeToken<List<ApolloConfigNotification>>() {}.getType();


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
    private WatchedKeyResultRepo watchedKeyResultRepo;


    /**
     * 客户端监听 服务端是否有变更
     * @param appId
     * @param cluster
     * @param env
     * @param notificationsAsString
     * @param dataCenter
     * @param clientIp
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> watchChanged(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "cluster") String cluster,
            @RequestParam(value = "env") String env,
            @RequestParam(value = "notifications") String notificationsAsString,
            @RequestParam(value = "dataCenter", required = false) String dataCenter,
            @RequestParam(value = "ip", required = false) String clientIp) {


        DeferredResultWrapper deferredResultWrapper = new DeferredResultWrapper();
        Set<String> namespaces4ClientInterested = Sets.newHashSet();

        Map<String, ApolloConfigNotification> clientNotificationMap = buildAndFilterClientNotification(notificationsAsString, appId);

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

    private void doAsyncCallbackMethodAndWactchedKeyRegistry(String appId, String cluster,String dataCenter, DeferredResultWrapper deferredResultWrapper, Set<String> namespaces, Set<String> clientWatchedKeys) {
        deferredResultWrapper.onTimeout(() -> logWatchedKeys(clientWatchedKeys, "Apollo.LongPoll.TimeOutKeys"));

        deferredResultWrapper.onCompletion(() -> {
            //unregister all keys
            for (String clientWatchedKey : clientWatchedKeys) {
                watchedKeyResultRepo.remove(clientWatchedKey, deferredResultWrapper);
            }
            logWatchedKeys(clientWatchedKeys, "Apollo.LongPoll.CompletedKeys");
        });

        registerWatchedKeys(deferredResultWrapper, clientWatchedKeys);

        log.debug("Listening {} from appCode: {}, cluster: {}, appNamespace: {}, datacenter: {}", clientWatchedKeys, appId, cluster, namespaces, dataCenter);
    }






    private void registerWatchedKeys(DeferredResultWrapper deferredResultWrapper, Set<String> watchedKeys) {
        //register all keys
        for (String key : watchedKeys) {
            this.watchedKeyResultRepo.put(key, deferredResultWrapper);
        }

        logWatchedKeys(watchedKeys, "Apollo.LongPoll.RegisteredKeys");
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

    private Map<String, ApolloConfigNotification> buildAndFilterClientNotification(String notificationsAsString, String appId) {
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
            String originalNamespace = namespaceUtil.subSuffix4Properties(notification.getNamespaceName());
            notification.setNamespaceName(originalNamespace);
            //fix the character case issue, such as FX.apollo <-> fx.apollo
            String normalizedNamespace = namespaceUtil.fixCapsLook4NamespaceName(appId, originalNamespace);

            // in case client side appNamespace name has character case issue and has difference notification ids
            // such as FX.apollo = 1 but fx.apollo = 2, we should let FX.apollo have the chance to update its notification id
            // which means we should record FX.apollo = 1 here and ignore fx.apollo = 2
            if (filteredNotifications.containsKey(normalizedNamespace) && filteredNotifications.get(normalizedNamespace).getNotificationId() < notification.getNotificationId()) {
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



    private void logWatchedKeys(Set<String> watchedKeys, String eventName) {
        for (String watchedKey : watchedKeys) {
//            Tracer.logEvent(eventName, watchedKey);
        }
    }
}


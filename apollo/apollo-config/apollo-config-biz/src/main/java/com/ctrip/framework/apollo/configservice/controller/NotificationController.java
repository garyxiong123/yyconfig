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

import com.ctrip.framework.apollo.configservice.controller.timer.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.configservice.util.WatchKeysUtil;
import com.ctrip.framework.apollo.configservice.util.EntityManagerUtil;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.yofish.apollo.component.constant.PermissionType;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.pattern.listener.releasemessage.ReleaseMessageListener;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.dto.NamespaceChangeNotification;
import framework.apollo.tracer.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Set;

@Deprecated
@RestController
@RequestMapping("/notifications")
public class NotificationController implements ReleaseMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private static final long TIMEOUT = 30 * 1000;//30 seconds
    private final Multimap<String, DeferredResult<ResponseEntity<NamespaceChangeNotification>>>
            deferredResults = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private static final ResponseEntity<NamespaceChangeNotification>
            NOT_MODIFIED_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    private static final Splitter STRING_SPLITTER =
            Splitter.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).omitEmptyStrings();

    @Autowired
    private WatchKeysUtil watchKeysUtil;

    @Autowired
    private ReleaseMessageServiceWithCache releaseMessageService;

    @Autowired
    private EntityManagerUtil entityManagerUtil;

    @Autowired
    private NamespaceUtil namespaceUtil;

    /**
     * For single appNamespace notification, reserved for older version of apollo clients
     *
     * @param appId          the appCode
     * @param cluster        the appEnvCluster
     * @param namespace      the appNamespace name
     * @param dataCenter     the datacenter
     * @param notificationId the notification id for the appNamespace
     * @param clientIp       the client side ip
     * @return a deferred result
     */
    @RequestMapping(method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<NamespaceChangeNotification>> pollNotification(
            @RequestParam(value = "appCode") String appId,
            @RequestParam(value = "appEnvCluster") String cluster,
            @RequestParam(value = "env") String env,
            @RequestParam(value = "appNamespace", defaultValue = ConfigConsts.NAMESPACE_APPLICATION) String namespace,
            @RequestParam(value = "dataCenter", required = false) String dataCenter,
            @RequestParam(value = "notificationId", defaultValue = "-1") long notificationId,
            @RequestParam(value = "ip", required = false) String clientIp) {
        //strip out .properties suffix
        namespace = namespaceUtil.subSuffix4Properties(namespace);

        Set<String> watchedKeys = watchKeysUtil.assembleAllWatchKeys(appId, cluster, env, namespace, dataCenter);

        DeferredResult<ResponseEntity<NamespaceChangeNotification>> deferredResult =
                new DeferredResult<>(TIMEOUT, NOT_MODIFIED_RESPONSE);

        //check whether client is out-dated
        ReleaseMessage latest = releaseMessageService.findLatestReleaseMessageForMessages(watchedKeys);

        /**
         * Manually close the entity manager.
         * Since for async request, Spring won't do so until the request is finished,
         * which is unacceptable since we are doing long polling - means the db connection would be hold
         * for a very long time
         */
        entityManagerUtil.closeEntityManager();

        if (latest != null && latest.getId() != notificationId) {
            deferredResult.setResult(new ResponseEntity<>(
                    new NamespaceChangeNotification(namespace, latest.getId()), HttpStatus.OK));
        } else {
            //register all keys
            for (String key : watchedKeys) {
                this.deferredResults.put(key, deferredResult);
            }

            deferredResult
                    .onTimeout(() -> logWatchedKeys(watchedKeys, "Apollo.LongPoll.TimeOutKeys"));

            deferredResult.onCompletion(() -> {
                //unregister all keys
                for (String key : watchedKeys) {
                    deferredResults.remove(key, deferredResult);
                }
                logWatchedKeys(watchedKeys, "Apollo.LongPoll.CompletedKeys");
            });

            logWatchedKeys(watchedKeys, "Apollo.LongPoll.RegisteredKeys");
            logger.debug("Listening {} from appCode: {}, appEnvCluster: {}, appNamespace: {}, datacenter: {}",
                    watchedKeys, appId, cluster, namespace, dataCenter);
        }

        return deferredResult;
    }

    @Override
    public void onReceiveReleaseMessage(ReleaseMessage message, String channel) {
        logger.info("message received - channel: {}, message: {}", channel, message);

        String namespaceKey = message.getNamespaceKey();
        Tracer.logEvent("Apollo.LongPoll.Messages", namespaceKey);
        if (!PermissionType.Topics.APOLLO_RELEASE_TOPIC.equals(channel) || Strings.isNullOrEmpty(namespaceKey)) {
            return;
        }
        List<String> keys = STRING_SPLITTER.splitToList(namespaceKey);
        //message should be appCode+appEnvCluster+appNamespace
        if (keys.size() != 3) {
            logger.error("message format invalid - {}", namespaceKey);
            return;
        }

        ResponseEntity<NamespaceChangeNotification> notification =
                new ResponseEntity<>(
                        new NamespaceChangeNotification(keys.get(2), message.getId()), HttpStatus.OK);

        if (!deferredResults.containsKey(namespaceKey)) {
            return;
        }
        //create a new list to avoid ConcurrentModificationException
        List<DeferredResult<ResponseEntity<NamespaceChangeNotification>>> results =
                Lists.newArrayList(deferredResults.get(namespaceKey));
        logger.debug("Notify {} clients for key {}", results.size(), namespaceKey);

        for (DeferredResult<ResponseEntity<NamespaceChangeNotification>> result : results) {
            result.setResult(notification);
        }
        logger.debug("Notification completed");
    }

    private void logWatchedKeys(Set<String> watchedKeys, String eventName) {
        for (String watchedKey : watchedKeys) {
            Tracer.logEvent(eventName, watchedKey);
        }
    }
}


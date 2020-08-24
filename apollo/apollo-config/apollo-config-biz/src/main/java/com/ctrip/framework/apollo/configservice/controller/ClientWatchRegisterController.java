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

import com.ctrip.framework.apollo.configservice.domain.ConfigClient4Version;
import com.ctrip.framework.apollo.configservice.util.EntityManagerUtil;
import com.ctrip.framework.apollo.configservice.wrapper.ClientConnection;
import com.google.common.collect.*;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;

import static com.ctrip.framework.apollo.configservice.controller.RegistryCenter.logWatchedKeys;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Controller
@RequestMapping("/notifications/v2")
@Slf4j
public class ClientWatchRegisterController {


    @Autowired
    private EntityManagerUtil entityManagerUtil;

    @Autowired
    private RegistryCenter registryCenter;


    /**
     * 客户端监听 服务端 ns 版本是否有变更
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public DeferredResult<ResponseEntity<List<NamespaceVersion>>> namespaceVersionsCompare(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "cluster") String cluster,
            @RequestParam(value = "env") String env,
            @RequestParam(value = "notifications") String clientNsVersionMapString,
            @RequestParam(value = "dataCenter", required = false) String dataCenter,
            @RequestParam(value = "ip", required = false) String clientIp) {

        ConfigClient4Version client4Version = new ConfigClient4Version(appId, cluster, env, dataCenter, clientIp, clientNsVersionMapString);

        ClientConnection clientConnection = new ClientConnection();
        Set<String> namespaces4Client = Sets.newHashSet();

        entityManagerUtil.closeEntityManager();

        List<NamespaceVersion> newNsVersions = client4Version.calcNewNsVersions();

        if (isNotEmpty(newNsVersions)) {
            doSyncResponse(clientConnection, newNsVersions);
            return clientConnection.getResponse();   // TODO 返回后断开连接，客户端继续连接
        }

        doAsyncResponseAndWactchedKeyRegistry(appId, cluster, dataCenter, clientConnection, namespaces4Client, client4Version.getClientWatchedKeys());
        return clientConnection.getResponse();
    }


    private void doSyncResponse(ClientConnection clientConnection, List<NamespaceVersion> newServerNotifications) {
        clientConnection.setResult(newServerNotifications);
    }

    private void doAsyncResponseAndWactchedKeyRegistry(String appId, String cluster, String dataCenter, ClientConnection clientConnection, Set<String> namespaces, Set<String> clientWatchedKeys) {
        clientConnection.onTimeout(() -> logWatchedKeys(clientWatchedKeys, "Apollo.LongPoll.TimeOutKeys"));

        clientConnection.onCompletion(() -> {
            //unregister all keys
            for (String clientWatchedKey : clientWatchedKeys) {
                registryCenter.remove(clientWatchedKey, clientConnection);
            }
            logWatchedKeys(clientWatchedKeys, "Apollo.LongPoll.CompletedKeys");
        });

        this.registryCenter.registerWatchedKeys(clientConnection, clientWatchedKeys);

        log.debug("Listening {} from appCode: {}, cluster: {}, appNamespace: {}, datacenter: {}", clientWatchedKeys, appId, cluster, namespaces, dataCenter);
    }


}


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
import com.ctrip.framework.apollo.configservice.cache.RegistryCenter;
import com.ctrip.framework.apollo.configservice.component.util.EntityManagerUtil;
import com.ctrip.framework.apollo.configservice.component.wrapper.ClientConnection;
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

import static com.ctrip.framework.apollo.configservice.cache.RegistryCenter.logWatchedKeys;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Controller
@RequestMapping("/notifications/v2")
@Slf4j
public class VersionCompareController {


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
    public DeferredResult<ResponseEntity<List<NamespaceVersion>>> getNewNsVersion(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "cluster") String cluster,
            @RequestParam(value = "env") String env,
            @RequestParam(value = "notifications") String clientNsVersionMapStr,
            @RequestParam(value = "dataCenter", required = false) String dataCenter,
            @RequestParam(value = "ip", required = false) String clientIp) {

        ConfigClient4Version client4Version = new ConfigClient4Version(appId, cluster, env, dataCenter, clientIp, clientNsVersionMapStr);

        ClientConnection clientConnection = new ClientConnection();
        Set<String> namespaces4Client = Sets.newHashSet();

        List<NamespaceVersion> newNsVersions = client4Version.calcNewNsVersions();

        if (hasNewNsVersion(newNsVersions)) {
            doSyncResponse(clientConnection, newNsVersions);
            return clientConnection.getResponse();   // TODO 返回后断开连接，客户端继续连接
        }

        doAsyncResponse(appId, cluster, dataCenter, clientConnection, namespaces4Client, client4Version.getLongNsNames());
        return clientConnection.getResponse();
    }

    private boolean hasNewNsVersion(List<NamespaceVersion> newNsVersions) {
        return isNotEmpty(newNsVersions);
    }


    private void doSyncResponse(ClientConnection clientConnection, List<NamespaceVersion> newServerNotifications) {
        clientConnection.setResult(newServerNotifications);
    }

    private void doAsyncResponse(String appId, String cluster, String dataCenter, ClientConnection clientConnection, Set<String> namespaces, Set<String> longNsNames) {
        registryCenter.registerConnByLongNsNames(clientConnection, longNsNames);

        clientConnection.onTimeout(() -> logWatchedKeys(longNsNames, "Apollo.LongPoll.TimeOutKeys"));

        clientConnection.onCompletion(() -> {
            //TODO  by wangsongjun
            registryCenter.unregister(longNsNames, clientConnection);//unregister all keys

            log.debug("Listening {} from appCode: {}, cluster: {}, appNamespace: {}, datacenter: {}", longNsNames, appId, cluster, namespaces, dataCenter);
        });
    }

}

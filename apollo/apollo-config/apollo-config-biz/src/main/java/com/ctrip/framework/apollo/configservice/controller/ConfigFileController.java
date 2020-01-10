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

import com.ctrip.framework.apollo.configservice.util.NamespaceUtil;
import com.ctrip.framework.apollo.configservice.util.WatchKeysUtil;
import com.google.common.cache.*;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.message.ReleaseMessageListener;
import com.yofish.apollo.message.Topics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;
import static common.utils.YyHttpUtils.tryToGetClientIp;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Slf4j
@RestController
@RequestMapping("/configfiles")
public class ConfigFileController implements ReleaseMessageListener {
    private static final long MAX_CACHE_SIZE = 50 * 1024 * 1024;
    private static final long EXPIRE_AFTER_WRITE = 30;
    private final HttpHeaders propertiesResponseHeaders;
    private final HttpHeaders jsonResponseHeaders;
    private final ResponseEntity<String> NOT_FOUND_RESPONSE;
    private Cache<String, String> localCache;
    private final Multimap<String, String> watchedKeys2CacheKey = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private final Multimap<String, String> cacheKey2WatchedKeys = Multimaps.synchronizedSetMultimap(HashMultimap.create());


    @Autowired
    private ConfigController configController;
    @Autowired
    private NamespaceUtil namespaceUtil;
    @Autowired
    private WatchKeysUtil watchKeysUtil;
//    @Autowired
//    private GrayReleaseRulesHolder grayReleaseRulesHolder;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;

    public ConfigFileController() {
        localCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_AFTER_WRITE, TimeUnit.MINUTES)
                .weigher(new Weigher<String, String>() {
                    @Override
                    public int weigh(String key, String value) {
                        return value == null ? 0 : value.length();
                    }
                })
                .maximumWeight(MAX_CACHE_SIZE)
                .removalListener(new RemovalListener<String, String>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, String> notification) {
                        String cacheKey = notification.getKey();
                        log.debug("removing cache key: {}", cacheKey);
                        if (!cacheKey2WatchedKeys.containsKey(cacheKey)) {
                            return;
                        }
                        //create a new list to avoid ConcurrentModificationException
                        List<String> watchedKeys = new ArrayList<>(cacheKey2WatchedKeys.get(cacheKey));
                        for (String watchedKey : watchedKeys) {
                            watchedKeys2CacheKey.remove(watchedKey, cacheKey);
                        }
                        cacheKey2WatchedKeys.removeAll(cacheKey);
                        log.debug("removed cache key: {}", cacheKey);
                    }
                })
                .build();
        propertiesResponseHeaders = new HttpHeaders();
        propertiesResponseHeaders.add("Content-Type", "text/plain;charset=UTF-8");
        jsonResponseHeaders = new HttpHeaders();
        jsonResponseHeaders.add("Content-Type", "application/json;charset=UTF-8");
        NOT_FOUND_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
/*
    @RequestMapping(value = "/{appCode}/{clusterName}/{namespace:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> queryConfigAsProperties(@PathVariable String appCode,
                                                          @PathVariable String clusterName,
                                                          @PathVariable String namespace,
                                                          @RequestParam(value = "dataCenter", required = false) String dataCenter,
                                                          @RequestParam(value = "ip", required = false) String clientIp) throws IOException {

        ConfigReqDto configReqDto4Properties = createConfigReqDto4Properties(appCode, clusterName, namespace, dataCenter, clientIp);
        String result = queryConfig(configReqDto4Properties);

        if (result == null) {
            return NOT_FOUND_RESPONSE;
        }

        return new ResponseEntity<>(result, propertiesResponseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/json/{appCode}/{clusterName}/{namespace:.+}", method = RequestMethod.GET)
    public ResponseEntity<String> queryConfigAsJson(@PathVariable String appCode,
                                                    @PathVariable String clusterName,
                                                    @PathVariable String namespace,
                                                    @RequestParam(value = "dataCenter", required = false) String dataCenter,
                                                    @RequestParam(value = "ip", required = false) String clientIp) throws IOException {
        ConfigReqDto configReqDto = createConfigReqDto4Json(appCode, clusterName, namespace, dataCenter, clientIp);
        String result = queryConfig(configReqDto);

        if (result == null) {
            return NOT_FOUND_RESPONSE;
        }

        return new ResponseEntity<>(result, jsonResponseHeaders, HttpStatus.OK);
    }*/



    @Override
    public void handleReleaseMessage(ReleaseMessage message, String channel) {
        log.info("message received - channel: {}, message: {}", channel, message);

        String content = message.getMessage();
        if (!Topics.APOLLO_RELEASE_TOPIC.equals(channel) || isNullOrEmpty(content)) {
            return;
        }

        if (!watchedKeys2CacheKey.containsKey(content)) {
            return;
        }

        //create a new list to avoid ConcurrentModificationException
        List<String> cacheKeys = new ArrayList<>(watchedKeys2CacheKey.get(content));

        for (String cacheKey : cacheKeys) {
            log.debug("invalidate cache key: {}", cacheKey);
            localCache.invalidate(cacheKey);
        }
    }

/*
    private String queryConfig(ConfigReqDto configReqDto) throws IOException {

        String queryConfigRs = null;
        String cacheKey = paramsCheckAndFilter(configReqDto);

        boolean hasGrayReleaseRule4CurrenClient = hasGrayRule4CurrentClient(configReqDto);
        if (hasGrayReleaseRule4CurrenClient) {
            return loadConfigByGrayRule(configReqDto);
        }

        if (cacheExistsThenLoad(cacheKey, queryConfigRs)) {
            return queryConfigRs;
        }

        if (isNullOrEmpty(queryConfigRs)) {

            queryConfigRs = loadConfigFromConfigController(configReqDto);

            if (queryConfigRs == null) {
                return null;
            }
            //5. Double check if this client needs to load gray release, if yes, load from db again
            //This step is mainly to avoid cache pollution
            if (grayReleaseRulesHolder.hasGrayReleaseRule(configReqDto.getAppCode(), configReqDto.getClientIp(), configReqDto.getNamespace())) {

                Tracer.logEvent("ConfigFile.Cache.GrayReleaseConflict", cacheKey);
                return loadConfigFromConfigController(configReqDto);
            }

            updateCache(configReqDto, queryConfigRs, cacheKey);
        }

        return queryConfigRs;
    }*/

    private String paramsCheckAndFilter(ConfigReqDto configReqDto) {
        namespaceUtil.filterAndNormalizeNamespace(configReqDto.getAppId(), configReqDto.getNamespace());
        String cacheKey = configReqDto.assembleCacheKey();

        if (isNullOrEmpty(configReqDto.getClientIp())) {
            configReqDto.setClientIp(tryToGetClientIp(request));
        }
        return cacheKey;

    }

    private void updateCache(ConfigReqDto configReqDto, String queryConfigRs, String cacheKey) {
        localCache.put(cacheKey, queryConfigRs);
        log.debug("adding cache for key: {}", cacheKey);

        Set<String> watchedKeys = watchKeysUtil.assembleAllWatchKeys(configReqDto.getAppId(), configReqDto.getClusterName(), configReqDto.getAppId(),configReqDto.getNamespace(), configReqDto.getDataCenter());

        for (String watchedKey : watchedKeys) {
            watchedKeys2CacheKey.put(watchedKey, cacheKey);
        }

        cacheKey2WatchedKeys.putAll(cacheKey, watchedKeys);
        log.debug("added cache for key: {}", cacheKey);
    }

    private boolean cacheExistsThenLoad(String cacheKey, String queryConfigRs) {
        queryConfigRs = localCache.getIfPresent(cacheKey);
        return queryConfigRs != null;
    }
/*
    private String loadConfigByGrayRule(ConfigReqDto configReqDto) throws IOException {
        return loadConfigFromConfigController(configReqDto);
    }*/

//

/*
    private String loadConfigFromConfigController(ConfigReqDto configReqDto) throws IOException {
        ApolloConfig apolloConfig = queryConfigController.queryConfig4Client(configReqDto.getAppCode(), configReqDto.getClusterName(), configReqDto.getNamespace(), configReqDto.getDataCenter(), "-1", configReqDto.getClientIp(), null, request, response);

        if (apolloConfig == null || apolloConfig.getConfigurations() == null) {
            return null;
        }
        String result = configReqDto.getConfigResult(apolloConfig.getConfigurations());

        return result;
    }*/

    private ConfigReqDto createConfigReqDto4Json( String appId, String clusterName, String namespace, String dataCenter, String clientIp) {
        return ConfigReqDto4Json.builder().appId(appId).clusterName(clusterName).namespace(namespace).dataCenter(dataCenter).clientIp(clientIp).build();
    }
    private ConfigReqDto createConfigReqDto4Properties( String appId, String clusterName, String namespace, String dataCenter, String clientIp) {
        return ConfigReqDto4Properties.builder().appId(appId).clusterName(clusterName).namespace(namespace).dataCenter(dataCenter).clientIp(clientIp).build();
    }



    enum ConfigFileOutputFormat {
        PROPERTIES("properties"), JSON("json");

        private String value;

        ConfigFileOutputFormat(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


}

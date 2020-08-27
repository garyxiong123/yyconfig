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
package com.ctrip.framework.apollo.configservice.component.util;

import com.ctrip.framework.apollo.configservice.cache.AppNamespaceCache;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.yofish.yyconfig.common.common.utils.YyStringUtils.notEqual;

/**
 * Namespace的 全名 组合
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class LongNamespaceNameUtil {
    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);
    @Autowired
    private AppNamespaceCache appNamespaceCache;

    /**
     * Assemble watch keys for the given appCode, appEnvCluster, appNamespace, dataCenter combination
     */
    public Set<String> assembleLongNamespaceNameSet(String appId, String clusterName, String env, String namespace,
                                                    String dataCenter) {
        Multimap<String, String> watchedKeysMap = assembleLongNamespaceNameMap(appId, clusterName, env, Sets.newHashSet(namespace), dataCenter);
        return Sets.newHashSet(watchedKeysMap.get(namespace));
    }

    /**
     * Assemble watch keys for the given appCode, appEnvCluster, namespaces, dataCenter combination
     *
     * @return a multimap with appNamespace as the key and watch keys as the value
     */
    public Multimap<String, String> assembleLongNamespaceNameMap(String appId, String clusterName, String env,
                                                                 Set<String> namespaces,
                                                                 String dataCenter) {

        Multimap<String, String> watchedKeysMap = HashMultimap.create();
        Set<String> namespacesBelongToAppId = null;
        Set<String> publicNamespaces = null;
        //Every app has an 'application' appNamespace  过滤调
        if (!(namespaces.size() == 1 && namespaces.contains(ConfigConsts.NAMESPACE_APPLICATION))) {
            namespacesBelongToAppId = appNamespaceCache.namespacesBelongToAppId(appId, namespaces);
            publicNamespaces = Sets.difference(namespaces, namespacesBelongToAppId);

            //Listen on more namespaces if it's a public appNamespace  放入 共有命名
            if (!publicNamespaces.isEmpty()) {
                watchedKeysMap.putAll(findPublicConfigWatchKeys(appId, clusterName, env, publicNamespaces, dataCenter));
            }
            //放入
            if (!namespacesBelongToAppId.isEmpty()) {
                watchedKeysMap.putAll(assembleLongNsNames(appId, clusterName, env, namespaces, dataCenter));
            }
        }


        return watchedKeysMap;
    }

    private Multimap<String, String> findPublicConfigWatchKeys(String applicationId,
                                                               String clusterName, String env,
                                                               Set<String> namespaces,
                                                               String dataCenter) {
        Multimap<String, String> watchedKeysMap = HashMultimap.create();
        List<AppNamespace> appNamespaces = appNamespaceCache.findPublicNamespacesByNames(namespaces);

        for (AppNamespace appNamespace : appNamespaces) {
            //check whether the appNamespace's appCode equals to current one
            if (Objects.equals(applicationId, appNamespace.getApp().getId())) {
                continue;
            }

            String publicAppCode = appNamespace.getApp().getAppCode();

            watchedKeysMap.putAll(appNamespace.getName(), assembleLongNsNames(publicAppCode, clusterName, env, appNamespace.getName(), dataCenter));
        }

        return watchedKeysMap;
    }

    private String assembleKey(String appId, String cluster, String env, String namespace) {
        return STRING_JOINER.join(appId, cluster, env, namespace);
    }

    private Set<String> assembleLongNsNames(String appId, String clusterName, String env, String namespace,
                                            String dataCenter) {
        if (ConfigConsts.NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
            return Collections.emptySet();
        }
        Set<String> watchedKeys = Sets.newHashSet();

        //watch specified appEnvCluster config change
        if (notEqual(ConfigConsts.CLUSTER_NAME_DEFAULT, clusterName)) {
            watchedKeys.add(assembleKey(appId, clusterName, env, namespace));
        }

        //watch data center config change
        if (!Strings.isNullOrEmpty(dataCenter) && !Objects.equals(dataCenter, clusterName)) {
            watchedKeys.add(assembleKey(appId, dataCenter, env, namespace));
        }

        //watch default appEnvCluster config change
        watchedKeys.add(assembleKey(appId, ConfigConsts.CLUSTER_NAME_DEFAULT, env, namespace));

        return watchedKeys;
    }

    private Multimap<String, String> assembleLongNsNames(String appId, String clusterName, String env, Set<String> namespaces, String dataCenter) {
        Multimap<String, String> watchedKeysMap = HashMultimap.create();
        for (String namespace : namespaces) {
            watchedKeysMap.putAll(namespace, assembleLongNsNames(appId, clusterName, env, namespace, dataCenter));
        }


        return watchedKeysMap;
    }


}

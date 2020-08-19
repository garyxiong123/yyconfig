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
package com.yofish.apollo.component.util;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Branch;
import framework.apollo.core.ConfigConsts;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * releaseMessage 生成逻辑
 */
public class NamespaceKeyGenerator {

    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);

    public static String generate(String appId, String cluster, String env, String namespace) {
        return STRING_JOINER.join(appId, cluster, env, namespace);
    }

    public static String generate(AppEnvClusterNamespace namespace) {
        String messageCluster;
        if (namespace instanceof AppEnvClusterNamespace4Branch) {
            messageCluster = ((AppEnvClusterNamespace4Branch) namespace).getMainNamespace().getAppEnvCluster().getName();
        } else {
            messageCluster = namespace.getAppEnvCluster().getName();
        }

        return generate(namespace.getAppNamespace().getApp().getAppCode(), messageCluster, namespace.getAppEnvCluster().getEnv().toLowerCase(), namespace.getAppNamespace().getName());
    }


    /**
     * 从 NamespaceKey 解析出 Namespace
     */
    private static final Function<String, String> getNamespaceFromNamespaceKey =
            releaseMessage -> {
                Splitter STRING_SPLITTER = Splitter.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).omitEmptyStrings();

                if (isNullOrEmpty(releaseMessage)) {
                    return null;
                }
                List<String> keys = STRING_SPLITTER.splitToList(releaseMessage);
                //message should be appCode+appEnvCluster+appNamespace
                if (keys.size() != 4) {
                    return null;
                }
                return keys.get(3);
            };


    public static String getNamespaceName(String namespaceKey) {
        return getNamespaceFromNamespaceKey.apply(namespaceKey);
    }
}

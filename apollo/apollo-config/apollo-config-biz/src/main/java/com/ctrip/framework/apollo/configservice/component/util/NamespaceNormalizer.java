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
import com.google.common.collect.Maps;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass4Context;

/**
 * @author Jason Song(song_s@ctrip.com)
 * 命名空间名称 标准化对象 所有关于 后缀处理和 大小写的 标准处理 计算
 */
@Component
public class NamespaceNormalizer {

    @Autowired
    private AppNamespaceCache appNamespaceCache;


    /**
     * //strip out .properties suffix 去除 .properties 尾缀
     *
     * @param namespaceName
     * @return
     */
    public String subSuffix4Properties(String namespaceName) {
        if (namespaceName.toLowerCase().endsWith(".properties")) {
            int dotIndex = namespaceName.lastIndexOf(".");
            return namespaceName.substring(0, dotIndex);
        }

        return namespaceName;
    }


    /**
     * //fix the character case issue, such as FX.apollo <-> fx.apollo: 处理 命名空间名称的 大小写 问题
     *
     * @param appId
     * @param namespaceName
     * @return
     */
    public String fixCapsLook4NamespaceName(String appId, String namespaceName) {
        AppNamespace appNamespace = appNamespaceCache.findByAppIdAndNamespace4Private(appId, namespaceName);
        if (appNamespace != null) {
            return appNamespace.getName();
        }

        appNamespace = appNamespaceCache.findPublicNamespaceByName(namespaceName);
        if (appNamespace != null) {
            return appNamespace.getName();
        }

        return namespaceName;
    }

    /**
     * 标准化命名空间的名称： 统一去掉后缀， 然后处理名称大小写
     *
     * @param appId
     * @param namespaceName
     * @return
     */
    public String normalizeNamespaceName(String appId, String namespaceName) {

        namespaceName = subSuffix4Properties(namespaceName);

        fixCapsLook4NamespaceName(appId, namespaceName);
        return namespaceName;

    }


    public Map<String, NamespaceVersion> normalizeNsVersions2Map(String appId, List<NamespaceVersion> namespaceVersions) {
        Map<String, NamespaceVersion> normalizedNsVersionMap = Maps.newHashMap();
        for (NamespaceVersion nsVersion4Client : namespaceVersions) {
            if (isNullOrEmpty(nsVersion4Client.getNamespaceName())) {
                continue;
            }
            //strip out .properties suffix

            String originalNamespace = getBeanByClass4Context(NamespaceNormalizer.class).subSuffix4Properties(nsVersion4Client.getNamespaceName());
            nsVersion4Client.setNamespaceName(originalNamespace);
            //fix the character case issue, such as FX.apollo <-> fx.apollo
            String normalizedNamespace = getBeanByClass4Context(NamespaceNormalizer.class).fixCapsLook4NamespaceName(appId, originalNamespace);

            // in case client side appNamespace name has character case issue and has difference notification ids
            // such as FX.apollo = 1 but fx.apollo = 2, we should let FX.apollo have the chance to update its notification id
            // which means we should record FX.apollo = 1 here and ignore fx.apollo = 2
            if (normalizedNsVersionMap.containsKey(normalizedNamespace) && normalizedNsVersionMap.get(normalizedNamespace).getReleaseMessageId() < nsVersion4Client.getReleaseMessageId()) {
                continue;
            }

            normalizedNsVersionMap.put(normalizedNamespace, nsVersion4Client);
        }
        return normalizedNsVersionMap;
    }

}

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
package com.ctrip.framework.apollo.configservice.util;

import com.ctrip.framework.apollo.configservice.controller.timer.AppNamespaceCache;
import com.yofish.apollo.domain.AppNamespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class NamespaceUtil {

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
        AppNamespace appNamespace = appNamespaceCache.findByAppIdAndNamespace(appId, namespaceName);
        if (appNamespace != null) {
            return appNamespace.getName();
        }

        appNamespace = appNamespaceCache.findPublicNamespaceByName(namespaceName);
        if (appNamespace != null) {
            return appNamespace.getName();
        }

        return namespaceName;
    }

    public String filterAndNormalizeNamespace(String appId, String namespaceName) {

        namespaceName = subSuffix4Properties(namespaceName);

        fixCapsLook4NamespaceName(appId, namespaceName);
        return namespaceName;

    }

}

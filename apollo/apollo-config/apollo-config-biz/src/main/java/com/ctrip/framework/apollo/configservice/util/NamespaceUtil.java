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

import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;
import com.yofish.apollo.domain.AppNamespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class NamespaceUtil {

    @Autowired
    private AppNamespaceServiceWithCache appNamespaceServiceWithCache;

    public String filterNamespaceName(String namespaceName) {
        if (namespaceName.toLowerCase().endsWith(".properties")) {
            int dotIndex = namespaceName.lastIndexOf(".");
            return namespaceName.substring(0, dotIndex);
        }

        return namespaceName;
    }

    public String normalizeNamespace(String appId, String namespaceName) {
        AppNamespace appNamespace = appNamespaceServiceWithCache.findByAppIdAndNamespace(appId, namespaceName);
        if (appNamespace != null) {
            return appNamespace.getName();
        }

        appNamespace = appNamespaceServiceWithCache.findPublicNamespaceByName(namespaceName);
        if (appNamespace != null) {
            return appNamespace.getName();
        }

        return namespaceName;
    }

    public String filterAndNormalizeNamespace(String appId, String namespaceName) {
        //strip out .properties suffix
        namespaceName = filterNamespaceName(namespaceName);
        //fix the character case issue, such as FX.apollo <-> fx.apollo
        normalizeNamespace(appId, namespaceName);
        return namespaceName;

    }

}

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

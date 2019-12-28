package com.yofish.apollo.util;


import com.google.common.base.Joiner;
import com.yofish.apollo.component.txtresolver.ConfigChangeContentBuilder;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Branch;
import framework.apollo.core.ConfigConsts;

public class ReleaseMessageKeyGenerator {

    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);

    public static String generate(String appId, String cluster,String env, String namespace) {
        return STRING_JOINER.join(appId, cluster,env, namespace);
    }

    public static String generate(AppEnvClusterNamespace namespace) {
        String messageCluster;
        if (namespace instanceof AppEnvClusterNamespace4Branch) {
            messageCluster = ((AppEnvClusterNamespace4Branch) namespace).getMainNamespace().getAppEnvCluster().getName();
        } else {
            messageCluster = namespace.getAppEnvCluster().getName();
        }

        return generate(namespace.getAppNamespace().getApp().getAppCode(), messageCluster,  namespace.getAppEnvCluster().getEnv(),namespace.getAppNamespace().getName());
    }
}

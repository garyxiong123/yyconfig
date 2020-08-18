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
package biz;


import com.yofish.apollo.component.config.ServerConfigKey;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.ServerConfig;

public class MockBeanFactory {

    public static AppEnvClusterNamespace mockNamespace(String appId, String clusterName, String namespaceName) {
        AppEnvClusterNamespace instance = new AppEnvClusterNamespace();

//    instance.setAppId(appCode);
//    instance.setClusterName(clusterName);
//    instance.setNamespaceName(namespaceName);

        return instance;
    }

    public static AppNamespace mockAppNamespace(String appId, String name, boolean isPublic) {
        AppNamespace instance = new AppNamespace();

//    instance.setAppId(appCode);
//    instance.setName(name);
//    instance.setPublic(isPublic);

        return instance;
    }

    public static ServerConfig mockServerConfig(String key, String value, String cluster) {
        ServerConfig instance = new ServerConfig();

        instance.setKey(ServerConfigKey.valueOf(key));
        instance.setValue(value);
//        instance.setCluster(cluster);

        return instance;
    }

    public static Release mockRelease(long releaseId, String releaseKey, String appId,
                                      String clusterName, String groupName, String configurations) {
        Release instance = null;

        instance.setId(releaseId);
//    instance.setReleaseKey(releaseKey);
//    instance.setAppId(appCode);
//    instance.setClusterName(clusterName);
//    instance.setNamespaceName(groupName);
//    instance.setConfigurations(configurations);

        return instance;
    }

}

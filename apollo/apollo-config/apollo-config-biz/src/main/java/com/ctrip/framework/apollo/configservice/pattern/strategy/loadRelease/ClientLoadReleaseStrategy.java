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
package com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease;


import com.yofish.apollo.domain.Release;
import com.yofish.yyconfig.common.framework.apollo.core.dto.ApolloNotificationMessages;

/**
 * @author Jason Song(song_s@ctrip.com)
 * @Description: 客户端加载配置策略
 */
public interface ClientLoadReleaseStrategy {

    /**
     * Load config
     *
     * @param clientAppId       the client's app id
     * @param clientIp          the client ip
     * @param configAppId       the requested config's app id
     * @param configClusterName the requested config's appEnvCluster name
     * @param configNamespace   the requested config's appNamespace name
     * @param dataCenter        the client data center
     * @param clientMessages    the messages received in client side
     * @return the Release
     */
    Release loadRelease4Client(String clientAppId, String clientIp, String configAppId, String
            configClusterName, String env, String configNamespace, String dataCenter, ApolloNotificationMessages clientMessages);
}

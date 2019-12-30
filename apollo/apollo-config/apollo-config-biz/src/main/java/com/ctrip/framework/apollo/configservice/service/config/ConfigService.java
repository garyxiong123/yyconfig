package com.ctrip.framework.apollo.configservice.service.config;


import com.yofish.apollo.domain.Release;
import com.yofish.apollo.message.ReleaseMessageListener;
import framework.apollo.core.dto.ApolloNotificationMessages;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ConfigService extends ReleaseMessageListener {

  /**
   * Load config
   *
   * @param clientAppId the client's app id
   * @param clientIp the client ip
   * @param configAppId the requested config's app id
   * @param configClusterName the requested config's appEnvCluster name
   * @param configNamespace the requested config's appNamespace name
   * @param dataCenter the client data center
   * @param clientMessages the messages received in client side
   * @return the Release
   */
  Release loadConfig4SingleClient(String clientAppId, String clientIp, String configAppId, String
          configClusterName, String env, String configNamespace, String dataCenter, ApolloNotificationMessages clientMessages);
}

package com.ctrip.framework.apollo.configservice.service.config;

import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.service.ReleaseService;
import framework.apollo.core.dto.ApolloNotificationMessages;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * config service with no cache
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigService extends AbstractConfigService {

  @Autowired
  private ReleaseService releaseService;

  @Override
  protected Release findActiveOne(long id, ApolloNotificationMessages clientMessages) {
    return releaseService.findActiveOne(id);
  }

  @Override
  protected Release findLatestActiveRelease(String configAppId, String configClusterName, String env, String configNamespace,
                                            ApolloNotificationMessages clientMessages) {
    return releaseService.findLatestActiveRelease(configAppId, configClusterName,env, configNamespace);
  }

  @Override
  public void handleReleaseMessage(ReleaseMessage message, String channel) {
    // since there is no cache, so do nothing
  }
}

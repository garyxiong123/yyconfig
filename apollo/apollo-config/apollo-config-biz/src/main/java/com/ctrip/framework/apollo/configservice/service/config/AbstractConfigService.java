package com.ctrip.framework.apollo.configservice.service.config;

import com.yofish.apollo.domain.Release;
import com.yofish.apollo.grayReleaseRule.GrayReleaseRulesHolder;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.dto.ApolloNotificationMessages;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public abstract class AbstractConfigService implements ConfigService {
  @Autowired
  private GrayReleaseRulesHolder grayReleaseRulesHolder;

  @Override
  public Release loadConfig4SingleClient(String clientAppId, String clientIp, String configAppId, String configClusterName, String env,
                                         String configNamespace, String dataCenter, ApolloNotificationMessages clientMessages) {
    // load from specified cluster fist
    if (!isDefaultCluster(configClusterName)) {
      Release clusterRelease = tryToLoadViaSpecifiedCluster(clientAppId, clientIp, configAppId, configClusterName,  env,configNamespace, clientMessages);
      if (!isNull(clusterRelease)) {return clusterRelease;}
    }

    if (isDataCenterValid(configClusterName, dataCenter)) {
      Release dataCenterRelease = tryToLoadViaDataCenter(clientAppId, clientIp, configAppId,env, configNamespace, dataCenter, clientMessages);
      if (!isNull(dataCenterRelease)) {
        return dataCenterRelease;
      }
    }

    // fallback to default release
    return loadReleaseViaDefaultCluster(clientAppId, clientIp, configAppId, env, configNamespace, clientMessages, ConfigConsts.CLUSTER_NAME_DEFAULT);
  }

  private Release loadReleaseViaDefaultCluster(String clientAppId, String clientIp, String configAppId, String env,  String configNamespace, ApolloNotificationMessages clientMessages, String clusterNameDefault) {
    return findRelease(clientAppId, clientIp, configAppId, env, clusterNameDefault, configNamespace,
            clientMessages);
  }

  private boolean isDataCenterValid(String configClusterName, String dataCenter) {
    return !isNullOrEmpty(dataCenter) && !Objects.equals(dataCenter, configClusterName);
  }

  private Release tryToLoadViaDataCenter(String clientAppId, String clientIp, String configAppId, String configNamespace, String env,String dataCenter, ApolloNotificationMessages clientMessages) {
    return findRelease(clientAppId, clientIp, configAppId, dataCenter, configNamespace, env, clientMessages);
  }

  private Release tryToLoadViaSpecifiedCluster(String clientAppId, String clientIp, String configAppId, String configClusterName, String env, String configNamespace, ApolloNotificationMessages clientMessages) {
    return findRelease(clientAppId, clientIp, configAppId, configClusterName,  env,configNamespace, clientMessages);
  }

  private boolean isDefaultCluster(String configClusterName) {
    return Objects.equals(ConfigConsts.CLUSTER_NAME_DEFAULT, configClusterName);
  }

  /**
   * Find release
   *
   * @param clientAppId the client's app id
   * @param clientIp the client ip
   * @param configAppId the requested config's app id
   * @param configClusterName the requested config's cluster name
   * @param configNamespace the requested config's appNamespace name
   * @param clientMessages the messages received in client side
   * @return the release
   */
  private Release findRelease(String clientAppId, String clientIp, String configAppId, String env, String configClusterName,
      String configNamespace, ApolloNotificationMessages clientMessages) {
    Long grayReleaseId = grayReleaseRulesHolder.findReleaseIdFromGrayReleaseRule(clientAppId, clientIp, configAppId,
        configClusterName, configNamespace);

    Release release = null;

    if (grayReleaseId != null) {
      release = findActiveOne(grayReleaseId, clientMessages);
    }

    if (release == null) {
      release = findLatestActiveRelease(configAppId, configClusterName, env, configNamespace, clientMessages);
    }

    return release;
  }

  /**
   * Find active release by id
   */
  protected abstract Release findActiveOne(long id, ApolloNotificationMessages clientMessages);

  /**
   * Find active release by app id, cluster name and appNamespace name
   */
  protected abstract Release findLatestActiveRelease(String configAppId, String configClusterName, String env,
      String configNamespaceName, ApolloNotificationMessages clientMessages);
}

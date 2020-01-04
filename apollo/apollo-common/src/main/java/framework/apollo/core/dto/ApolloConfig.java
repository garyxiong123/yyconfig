package framework.apollo.core.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Data
public class ApolloConfig {

  private String appId;

  private String cluster;

  private String namespaceName;

  private Map<String, String> configurations;

  private String releaseKey;

  public ApolloConfig() {
  }

  public ApolloConfig(String appId,
                      String cluster,
                      String namespaceName,
                      String releaseKey) {
    this.appId = appId;
    this.cluster = cluster;
    this.namespaceName = namespaceName;
    this.releaseKey = releaseKey;
  }


  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ApolloConfig{");
    sb.append("appCode='").append(appId).append('\'');
    sb.append(", appEnvCluster='").append(cluster).append('\'');
    sb.append(", namespaceName='").append(namespaceName).append('\'');
    sb.append(", configurations=").append(configurations);
    sb.append(", releaseKey='").append(releaseKey).append('\'');
    sb.append('}');
    return sb.toString();
  }
}

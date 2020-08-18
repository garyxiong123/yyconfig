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
package framework.apollo.core.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.List;
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

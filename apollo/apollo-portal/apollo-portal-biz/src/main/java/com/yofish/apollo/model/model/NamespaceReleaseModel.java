package com.yofish.apollo.model.model;


import framework.apollo.core.enums.Env;
import common.utils.YyStringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NamespaceReleaseModel implements Verifiable {

  private Long AppEnvClusterNamespaceId;
  private String releaseTitle;
  private String releaseComment;
  private boolean isEmergencyPublish;

  @Override
  public boolean isInvalid() {
    return YyStringUtils.isContainEmpty(String.valueOf(AppEnvClusterNamespaceId), releaseTitle);
  }


}

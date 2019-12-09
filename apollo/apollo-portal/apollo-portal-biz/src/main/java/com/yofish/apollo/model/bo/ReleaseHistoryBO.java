package com.yofish.apollo.model.bo;


import common.entity.EntityPair;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ReleaseHistoryBO {

  private long id;

  private String appId;

  private String clusterName;

  private String namespaceName;

  private String branchName;

  private String operator;

  private long releaseId;

  private String releaseTitle;

  private String releaseComment;

  private Date releaseTime;

  private String releaseTimeFormatted;

  private List<EntityPair<String>> configuration;

  private long previousReleaseId;

  private int operation;

  private Map<String, Object> operationContext;

}

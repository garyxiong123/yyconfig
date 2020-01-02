package com.yofish.apollo.model.vo;


import lombok.Data;

@Data
public class NamespaceIdentifier {
  private Long appEnvClusterId;
  private String env;
  private String clusterName;
  private String namespaceName;
}

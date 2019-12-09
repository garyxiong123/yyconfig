package com.yofish.apollo.model.model;


import common.dto.NamespaceDTO;

public class NamespaceCreationModel {

  private String env;

  private NamespaceDTO namespace;

  public String getEnv() {
    return env;
  }

  public void setEnv(String env) {
    this.env = env;
  }

  public NamespaceDTO getNamespace() {
    return namespace;
  }

  public void setNamespace(NamespaceDTO namespace) {
    this.namespace = namespace;
  }
}

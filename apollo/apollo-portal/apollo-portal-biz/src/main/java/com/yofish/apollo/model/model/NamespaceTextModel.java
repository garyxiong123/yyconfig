package com.yofish.apollo.model.model;


import framework.apollo.core.enums.ConfigFileFormat;
import framework.apollo.core.enums.Env;
import common.utils.YyStringUtils;

public class NamespaceTextModel implements Verifiable {

  private String appId;
  private String env;
  private String clusterName;
  private String namespaceName;
  private long namespaceId;
  private String format;
  private String configText;


  @Override
  public boolean isInvalid() {
    return YyStringUtils.isContainEmpty(appId, env, clusterName, namespaceName) || namespaceId <= 0;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public Env getEnv() {
    return Env.fromString(env);
  }

  public void setEnv(String env) {
    this.env = env;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public void setNamespaceName(String namespaceName) {
    this.namespaceName = namespaceName;
  }

  public long getNamespaceId() {
    return namespaceId;
  }

  public void setNamespaceId(long namespaceId) {
    this.namespaceId = namespaceId;
  }

  public String getConfigText() {
    return configText;
  }

  public void setConfigText(String configText) {
    this.configText = configText;
  }

  public ConfigFileFormat getFormat() {
    return ConfigFileFormat.fromString(this.format);
  }

  public void setFormat(String format) {
    this.format = format;
  }
}

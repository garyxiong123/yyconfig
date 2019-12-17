package com.yofish.apollo.listener;

import com.google.common.base.Preconditions;
import com.yofish.apollo.domain.AppNamespace;
import org.springframework.context.ApplicationEvent;

public class AppNamespaceCreationEvent extends ApplicationEvent {

  public AppNamespaceCreationEvent(Object source) {
    super(source);
  }

  public AppNamespace getAppNamespace() {
    Preconditions.checkState(source != null);
    return (AppNamespace) this.source;
  }
}

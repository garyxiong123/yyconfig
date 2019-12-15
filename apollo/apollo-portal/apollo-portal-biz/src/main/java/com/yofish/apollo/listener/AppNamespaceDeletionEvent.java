package com.yofish.apollo.listener;

import com.google.common.base.Preconditions;
import com.yofish.apollo.domain.AppNamespace;
import org.springframework.context.ApplicationEvent;

public class AppNamespaceDeletionEvent extends ApplicationEvent {

  public AppNamespaceDeletionEvent(Object source) {
    super(source);
  }

  public AppNamespace getAppNamespace() {
    Preconditions.checkState(source != null);
    return (AppNamespace) this.source;
  }
}

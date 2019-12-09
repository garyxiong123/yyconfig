package com.yofish.apollo.model.vo;

public class LockInfo {

  private String lockOwner;
  private boolean isEmergencyPublishAllowed;

  public String getLockOwner() {
    return lockOwner;
  }

  public void setLockOwner(String lockOwner) {
    this.lockOwner = lockOwner;
  }

  public boolean isEmergencyPublishAllowed() {
    return isEmergencyPublishAllowed;
  }

  public void setEmergencyPublishAllowed(boolean emergencyPublishAllowed) {
    isEmergencyPublishAllowed = emergencyPublishAllowed;
  }
}

package com.yofish.apollo.model.vo;

public class PageSetting {

  private String wikiAddress;

  private boolean canAppAdminCreatePrivateNamespace;

  public String getWikiAddress() {
    return wikiAddress;
  }

  public void setWikiAddress(String wikiAddress) {
    this.wikiAddress = wikiAddress;
  }

  public boolean isCanAppAdminCreatePrivateNamespace() {
    return canAppAdminCreatePrivateNamespace;
  }

  public void setCanAppAdminCreatePrivateNamespace(boolean canAppAdminCreatePrivateNamespace) {
    this.canAppAdminCreatePrivateNamespace = canAppAdminCreatePrivateNamespace;
  }
}

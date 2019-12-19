package com.yofish.apollo.model.bo;


import common.dto.NamespaceDTO;
import lombok.Data;

import java.util.List;

@Data
public class NamespaceVO {
  private NamespaceDTO baseInfo;
  private int itemModifiedCnt;
  private List<ItemBO> items;
  private String format;
  private boolean isPublic;
  private String parentAppCode;
  private String comment;
  // is the configs hidden to current user?
  private boolean isConfigHidden;


  public void hideItems() {
    setConfigHidden(true);
    items.clear();
    setItemModifiedCnt(0);
  }
}

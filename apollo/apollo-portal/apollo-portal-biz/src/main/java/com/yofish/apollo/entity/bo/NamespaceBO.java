package com.yofish.apollo.entity.bo;


import common.dto.NamespaceDTO;
import lombok.Data;

import java.util.List;

@Data
public class NamespaceBO {
  private NamespaceDTO baseInfo;
  private int itemModifiedCnt;
  private List<ItemBO> items;
  private String format;
  private boolean isPublic;
  private String parentAppId;
  private String comment;
  // is the configs hidden to current user?
  private boolean isConfigHidden;


  public void hideItems() {
    setConfigHidden(true);
    items.clear();
    setItemModifiedCnt(0);
  }
}

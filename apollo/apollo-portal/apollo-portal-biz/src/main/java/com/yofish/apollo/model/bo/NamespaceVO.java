/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.apollo.model.bo;


import com.yofish.apollo.api.model.bo.ItemBO;
import com.yofish.apollo.enums.NamespaceType;
import common.dto.NamespaceDTO;
import lombok.Data;

import java.util.List;

@Data
public class NamespaceVO {
  private NamespaceDTO baseInfo;
  private int itemModifiedCnt;
  private List<ItemBO> items;
  private String format;
  private NamespaceType namespaceType;
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

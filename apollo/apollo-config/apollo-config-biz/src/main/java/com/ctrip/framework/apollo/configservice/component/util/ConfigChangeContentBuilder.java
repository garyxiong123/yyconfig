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
package com.ctrip.framework.apollo.configservice.component.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yofish.apollo.domain.Item;
import com.yofish.yyconfig.common.common.utils.YyStringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class ConfigChangeContentBuilder {

  private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

  private List<Item> createItems = new LinkedList<>();
  private List<ItemPair> updateItems = new LinkedList<>();
  private List<Item> deleteItems = new LinkedList<>();


  public ConfigChangeContentBuilder createItem(Item item) {
    if (!YyStringUtils.isEmpty(item.getKey())){
      createItems.add(cloneItem(item));
    }
    return this;
  }

  public ConfigChangeContentBuilder updateItem(Item oldItem, Item newItem) {
    if (!oldItem.getValue().equals(newItem.getValue())){
      ItemPair itemPair = new ItemPair(cloneItem(oldItem), cloneItem(newItem));
      updateItems.add(itemPair);
    }
    return this;
  }

  public ConfigChangeContentBuilder deleteItem(Item item) {
    if (!YyStringUtils.isEmpty(item.getKey())) {
      deleteItems.add(cloneItem(item));
    }
    return this;
  }

  public boolean hasContent(){
    return !createItems.isEmpty() || !updateItems.isEmpty() || !deleteItems.isEmpty();
  }

  public String build() {
    //因为事务第一段提交并没有更新时间,所以build时统一更新
    Date now = new Date();

//    for (Item item : createItems) {
//      item.setDataChangeLastModifiedTime(now);
//    }
//
//    for (ItemPair item : updateItems) {
//      item.newItem.setDataChangeLastModifiedTime(now);
//    }
//
//    for (Item item : deleteItems) {
//      item.setDataChangeLastModifiedTime(now);
//    }
    return gson.toJson(this);
  }

  static class ItemPair {

    Item oldItem;
    Item newItem;

    public ItemPair(Item oldItem, Item newItem) {
      this.oldItem = oldItem;
      this.newItem = newItem;
    }
  }

  Item cloneItem(Item source) {
    Item target = new Item();

    BeanUtils.copyProperties(source, target);

    return target;
  }

}

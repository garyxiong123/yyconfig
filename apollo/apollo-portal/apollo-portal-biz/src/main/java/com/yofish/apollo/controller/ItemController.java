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
package com.yofish.apollo.controller;


import com.yofish.apollo.model.bo.ItemChangeSetDto;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.api.dto.*;
import com.yofish.apollo.model.NamespaceSyncModel;
import com.yofish.apollo.model.bo.ItemDiffs;
import com.yofish.apollo.model.NamespaceTextModel;
import com.yofish.apollo.service.ItemService;
import com.youyu.common.api.Result;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.yofish.yyconfig.common.common.utils.RequestPrecondition.checkModel;


@RestController
@RequestMapping("item")
public class ItemController {

  @Autowired
  private ItemService itemService;


  @PostMapping(value = "/modifyItemsByTexts")
  public Result modifyItemsByText(@RequestBody ModifyItemsByTextsReq model) {

    checkModel(model != null);
    itemService.updateConfigItemByText(model);
    return Result.ok();
  }


  @PostMapping(value = "/createItem")
  public Result<Item> createItem(@RequestBody CreateItemReq req) {
    itemService.createItem(req);
    return Result.ok();
  }


  @PostMapping(value = "/updateItem")
  public Result updateItem(@RequestBody UpdateItemReq req) {
     itemService.updateItem(req);
    return Result.ok();
  }



  @PostMapping(value = "deleteItem")
  public Result deleteItem(@RequestBody ItemReq req) {
    if (req.getItemId() <= 0) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "item id invalid");
    }
    itemService.deleteItem(req);
    return Result.ok();
  }


  @PostMapping(value = "/findItems")
  public Result<List<Item>> findItems(@RequestBody ItemReq req) {
    List<Item> items = itemService.findItemsWithoutOrdered(req);
    return Result.ok(items);
  }

//todo 配置同步

  @PostMapping(value = "updateEnv")
  public Result updateEnv(@RequestBody NamespaceSyncModel model) {
    itemService.syncItems(model.getSyncToNamespaces(), model.getSyncItems());
   return Result.ok();
  }
  @PostMapping(value = "/diff")
  public Result<List<ItemChangeSetDto>> diff(@RequestBody NamespaceSyncModel model) {
    List<ItemChangeSetDto> itemChangeSetDtos=new ArrayList<>();
    List<ItemDiffs> itemDiffs = itemService.compare(model.getSyncToNamespaces(), model.getSyncItems());
    for(ItemDiffs item:itemDiffs){
      ItemChangeSetDto dto=new ItemChangeSetDto();
      dto.setNamespace(item.getNamespace());
      dto.setDiffs(itemService.itemSetBuild(item.getDiffs()));
      dto.setExtInfo(item.getExtInfo());
      itemChangeSetDtos.add(dto);
    }

    return Result.ok(itemChangeSetDtos);
  }
  @PostMapping(value = "syntax-check")
  public Result syntaxCheckText(@RequestBody NamespaceTextModel model) {

    doSyntaxCheck(model);

    return Result.ok();
  }

  private void doSyntaxCheck(NamespaceTextModel model) {
    if (StringUtils.isBlank(model.getConfigText())) {
      return;
    }

    // only support yaml syntax check
    if (model.getFormat() != ConfigFileFormat.YAML.getValue() && model.getFormat() != ConfigFileFormat.YML.getValue()) {
      return;
    }

    // use YamlPropertiesFactoryBean to check the yaml syntax
    YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
    yamlPropertiesFactoryBean.setResources(new ByteArrayResource(model.getConfigText().getBytes()));
    // this call converts yaml to properties and will throw exception if the conversion fails
    yamlPropertiesFactoryBean.getObject();
  }


}

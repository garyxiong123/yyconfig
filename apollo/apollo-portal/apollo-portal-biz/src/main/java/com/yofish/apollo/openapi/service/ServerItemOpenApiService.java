/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.yofish.apollo.openapi.service;

import com.yofish.apollo.api.dto.CreateItemReq;
import com.yofish.apollo.api.dto.UpdateItemReq;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.openapi.util.OpenApiBeanUtils;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.service.ItemService;
import com.yofish.platform.yyconfig.openapi.api.ItemOpenApiService;
import com.yofish.platform.yyconfig.openapi.dto.OpenItemDTO;
import com.yofish.yyconfig.common.common.utils.BeanUtils;
import com.yofish.yyconfig.common.framework.apollo.core.enums.Env;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

/**
 * @author wxq
 */
@Service
public class ServerItemOpenApiService implements ItemOpenApiService {

  private final ItemService itemService;
  private final AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;

  public ServerItemOpenApiService(ItemService itemService, AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository) {
    this.itemService = itemService;
    this.appEnvClusterNamespaceRepository = appEnvClusterNamespaceRepository;
  }

  @Override
  public OpenItemDTO getItem(String appId, String env, String clusterName, String namespaceName, String key) {
    Item itemDTO = itemService.findOne(appId,Env.valueOf(env).name(), namespaceName, clusterName, AppEnvClusterNamespace.Type.Main.getValue(), key);
    return itemDTO == null ? null : OpenApiBeanUtils.transformFromItemDTO(itemDTO);
  }

  @Override
  public OpenItemDTO createItem(String appId, String env, String clusterName, String namespaceName,
      OpenItemDTO itemDTO) {
    AppEnvClusterNamespace appEnvClusterNamespace = appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(appId, Env.valueOf(env).name(), namespaceName, clusterName, AppEnvClusterNamespace.Type.Main.getValue());
    CreateItemReq createItemReq = new CreateItemReq(itemDTO.getKey(), itemDTO.getValue(), itemDTO.getComment(), Arrays.asList(appEnvClusterNamespace.getId()), null);

    itemService.createItem(createItemReq);
    return BeanUtils.transform(OpenItemDTO.class, itemDTO);
  }

  @Override
  public void updateItem(String appId, String env, String clusterName, String namespaceName, OpenItemDTO itemDTO) {
    Item toUpdateItem = itemService.findOne(appId,Env.valueOf(env).name(), namespaceName, clusterName, AppEnvClusterNamespace.Type.Main.getValue(), itemDTO.getKey());
    //protect. only value,comment,lastModifiedBy can be modified
    toUpdateItem.setComment(itemDTO.getComment());
    toUpdateItem.setValue(itemDTO.getValue());
    toUpdateItem.setUpdateAuthor(itemDTO.getDataChangeLastModifiedBy());
    UpdateItemReq updateItemReq = new UpdateItemReq();
    updateItemReq.setItemId(toUpdateItem.getId());
    updateItemReq.setValue(itemDTO.getValue());
    itemService.updateItem(updateItemReq);
  }

  @Override
  public void createOrUpdateItem(String appId, String env, String clusterName, String namespaceName, OpenItemDTO itemDTO) {
    Item toUpdateItem = itemService.findOne(appId, Env.valueOf(env).name(), namespaceName, clusterName, AppEnvClusterNamespace.Type.Main.getValue(), itemDTO.getKey());
    if (ObjectUtils.isEmpty(toUpdateItem)) {
      this.createItem(appId, env, clusterName, namespaceName, itemDTO);
    } else {
      this.updateItem(appId, env, clusterName, namespaceName, itemDTO);
    }
  }

  @Override
  public void removeItem(String appId, String env, String clusterName, String namespaceName,
      String key, String operator) {
//    ItemDTO toDeleteItem = this.itemService.loadItem(Env.valueOf(env), appId, clusterName, namespaceName, key);
//    this.itemService.deleteItem(Env.valueOf(env), toDeleteItem.getId(), operator);
  }
}

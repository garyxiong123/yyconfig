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
package com.yofish.apollo.openapi.controller;

//import com.ctrip.framework.apollo.common.dto.ItemDTO;
//import com.ctrip.framework.apollo.common.exception.BadRequestException;
//import com.ctrip.framework.apollo.common.utils.RequestPrecondition;
//import com.ctrip.framework.apollo.core.utils.StringUtils;
//import com.ctrip.framework.apollo.openapi.api.ItemOpenApiService;
//import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
//import com.ctrip.framework.apollo.portal.environment.Env;
//import com.ctrip.framework.apollo.portal.service.ItemService;
//import com.ctrip.framework.apollo.portal.spi.UserService;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.service.ItemService;
import com.yofish.gary.biz.service.UserService;
import com.yofish.platform.yyconfig.openapi.api.ItemOpenApiService;
import com.yofish.platform.yyconfig.openapi.dto.OpenItemDTO;
import com.yofish.yyconfig.common.common.dto.ItemDTO;
import com.yofish.yyconfig.common.common.utils.RequestPrecondition;
import com.yofish.yyconfig.common.common.utils.StringUtils;
import com.yofish.yyconfig.common.framework.apollo.core.enums.Env;
import com.youyu.common.exception.BizException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController("openapiItemController")
@RequestMapping("/openapi/v1/envs/{env}")
public class ItemController {

  private final ItemService itemService;
  private final UserService userService;
  private final ItemOpenApiService itemOpenApiService;

  public ItemController(final ItemService itemService, final UserService userService,
      ItemOpenApiService itemOpenApiService) {
    this.itemService = itemService;
    this.userService = userService;
    this.itemOpenApiService = itemOpenApiService;
  }

  @GetMapping(value = "/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items/{key:.+}")
  public OpenItemDTO getItem(@PathVariable String appId, @PathVariable String env, @PathVariable String clusterName,
                             @PathVariable String namespaceName, @PathVariable String key) {
    return this.itemOpenApiService.getItem(appId, env, clusterName, namespaceName, key);
  }

  @PreAuthorize(value = "@consumerPermissionValidator.hasModifyNamespacePermission(#request, #appId, #namespaceName, #env)")
  @PutMapping(value = "/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items/{key:.+}")
  public void updateItem(@PathVariable String appId, @PathVariable String env,
                         @PathVariable String clusterName, @PathVariable String namespaceName,
                         @PathVariable String key, @RequestBody OpenItemDTO item,
                         @RequestParam(defaultValue = "false") boolean createIfNotExists, HttpServletRequest request) {

    RequestPrecondition.checkArguments(item != null, "item payload can not be empty");

    RequestPrecondition.checkArguments(
        !StringUtils.isContainEmpty(item.getKey(), item.getDataChangeLastModifiedBy()),
        "key and dataChangeLastModifiedBy can not be empty");

    RequestPrecondition.checkArguments(item.getKey().equals(key), "Key in path and payload is not consistent");

    if (userService.findByUserId(item.getDataChangeLastModifiedBy()) == null) {
      throw new BizException("user(dataChangeLastModifiedBy) not exists");
    }

    if(!StringUtils.isEmpty(item.getComment()) && item.getComment().length() > 256){
      throw new BizException("Comment length should not exceed 256 characters");
    }

    if (createIfNotExists) {
      this.itemOpenApiService.createOrUpdateItem(appId, env, clusterName, namespaceName, item);
    } else {
      this.itemOpenApiService.updateItem(appId, env, clusterName, namespaceName, item);
    }
  }


  @PreAuthorize(value = "@consumerPermissionValidator.hasModifyNamespacePermission(#request, #appId, #namespaceName, #env)")
  @DeleteMapping(value = "/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/items/{key:.+}")
  public void deleteItem(@PathVariable String appId, @PathVariable String env,
                         @PathVariable String clusterName, @PathVariable String namespaceName,
                         @PathVariable String key, @RequestParam String operator,
                         HttpServletRequest request) {

    if (userService.findByUserId(operator) == null) {
      throw new BizException("user(operator) not exists");
    }

    Item itemDTO = itemService.findOne(appId,Env.valueOf(env).name(), namespaceName, clusterName, AppEnvClusterNamespace.Type.Main.getValue(), key);
    if (itemDTO == null){
      throw new BizException("item not exists");
    }

    this.itemOpenApiService.removeItem(appId, env, clusterName, namespaceName, key, operator);
  }

}

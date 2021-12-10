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

import com.yofish.gary.biz.service.UserService;
import com.yofish.platform.yyconfig.openapi.api.ClusterOpenApiService;
import com.yofish.platform.yyconfig.openapi.dto.OpenClusterDTO;
import com.yofish.yyconfig.common.common.utils.InputValidator;
import com.yofish.yyconfig.common.common.utils.RequestPrecondition;
import com.yofish.yyconfig.common.common.utils.StringUtils;
import com.youyu.common.exception.BizException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

@RestController("openapiClusterController")
@RequestMapping("/openapi/v1/envs/{env}")
public class ClusterController {

  private final UserService userService;
  private final ClusterOpenApiService clusterOpenApiService;

  public ClusterController(
      UserService userService,
      ClusterOpenApiService clusterOpenApiService) {
    this.userService = userService;
    this.clusterOpenApiService = clusterOpenApiService;
  }

  @GetMapping(value = "apps/{appId}/clusters/{clusterName:.+}")
  public OpenClusterDTO getCluster(@PathVariable("appId") String appId, @PathVariable String env,
                                   @PathVariable("clusterName") String clusterName) {
    return this.clusterOpenApiService.getCluster(appId, env, clusterName);
  }

  @PreAuthorize(value = "@consumerPermissionValidator.hasCreateClusterPermission(#request, #appId)")
  @PostMapping(value = "apps/{appId}/clusters")
  public OpenClusterDTO createCluster(@PathVariable String appId, @PathVariable String env,
      @Valid @RequestBody OpenClusterDTO cluster, HttpServletRequest request) {

    if (!Objects.equals(appId, cluster.getAppId())) {
      throw new BizException(String.format(
          "AppId not equal. AppId in path = %s, AppId in payload = %s", appId, cluster.getAppId()));
    }

    String clusterName = cluster.getName();
    String operator = cluster.getDataChangeCreatedBy();

    RequestPrecondition.checkArguments(!StringUtils.isContainEmpty(clusterName, operator),
        "name and dataChangeCreatedBy should not be null or empty");

    if (!InputValidator.isValidClusterNamespace(clusterName)) {
      throw new BizException(
          String.format("Invalid ClusterName format: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
    }

    if (userService.findByUserId(operator) == null) {
      throw new BizException("User " + operator + " doesn't exist!");
    }

    return this.clusterOpenApiService.createCluster(env, cluster);
  }

}

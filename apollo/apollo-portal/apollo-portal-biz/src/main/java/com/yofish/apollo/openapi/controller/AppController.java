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

import com.yofish.apollo.openapi.service.ConsumerService;
import com.yofish.apollo.openapi.util.ConsumerAuthUtil;
import com.yofish.platform.yyconfig.openapi.api.AppOpenApiService;
import com.yofish.platform.yyconfig.openapi.dto.OpenAppDTO;
import com.yofish.platform.yyconfig.openapi.dto.OpenEnvClusterDTO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController("openapiAppController")
@RequestMapping("/openapi/v1")
public class AppController {

  private final ConsumerAuthUtil consumerAuthUtil;
  private final ConsumerService consumerService;
  private final AppOpenApiService appOpenApiService;

  public AppController(
      final ConsumerAuthUtil consumerAuthUtil,
      final ConsumerService consumerService,
      AppOpenApiService appOpenApiService) {
    this.consumerAuthUtil = consumerAuthUtil;
    this.consumerService = consumerService;
    this.appOpenApiService = appOpenApiService;
  }

  @GetMapping(value = "/apps/{appId}/envclusters")
  public List<OpenEnvClusterDTO> getEnvClusterInfo(@PathVariable String appId){
    return this.appOpenApiService.getEnvClusterInfo(appId);
  }

  @GetMapping("/apps")
  public List<OpenAppDTO> findApps(@RequestParam(value = "appIds", required = false) String appIds) {
    if (StringUtils.hasText(appIds)) {
      return this.appOpenApiService.getAppsInfo(Arrays.asList(appIds.split(",")));
    } else {
      return this.appOpenApiService.getAllApps();
    }
  }

  /**
   * @return which apps can be operated by open api
   */
  @GetMapping("/apps/authorized")
  public List<OpenAppDTO> findAppsAuthorized(HttpServletRequest request) {
    long consumerId = this.consumerAuthUtil.retrieveConsumerId(request);

    Set<String> appIds = this.consumerService.findAppIdsAuthorizedByConsumerId(consumerId);

    return this.appOpenApiService.getAppsInfo(new ArrayList<>(appIds));
  }

}

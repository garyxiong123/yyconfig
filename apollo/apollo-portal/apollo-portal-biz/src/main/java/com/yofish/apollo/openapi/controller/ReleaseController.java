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

//import com.ctrip.framework.apollo.common.dto.ReleaseDTO;
//import com.ctrip.framework.apollo.common.exception.BizException;
//import com.ctrip.framework.apollo.common.utils.BeanUtils;
//import com.ctrip.framework.apollo.common.utils.RequestPrecondition;
//import com.ctrip.framework.apollo.core.utils.StringUtils;
//import com.ctrip.framework.apollo.openapi.api.ReleaseOpenApiService;
//import com.ctrip.framework.apollo.openapi.auth.ConsumerPermissionValidator;
//import com.ctrip.framework.apollo.openapi.dto.NamespaceGrayDelReleaseDTO;
//import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
//import com.ctrip.framework.apollo.openapi.dto.OpenReleaseDTO;
//import com.ctrip.framework.apollo.openapi.util.OpenApiBeanUtils;
//import com.ctrip.framework.apollo.portal.entity.model.NamespaceGrayDelReleaseModel;
//import com.ctrip.framework.apollo.portal.entity.model.NamespaceReleaseModel;
//import com.ctrip.framework.apollo.portal.environment.Env;
//import com.ctrip.framework.apollo.portal.service.NamespaceBranchService;
//import com.ctrip.framework.apollo.portal.service.ReleaseService;
//import com.ctrip.framework.apollo.portal.spi.UserService;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.model.NamespaceReleaseModel;
import com.yofish.apollo.openapi.auth.ConsumerPermissionValidator;
import com.yofish.apollo.openapi.util.OpenApiBeanUtils;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.service.NamespaceBranchService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.gary.biz.service.UserService;
import com.yofish.platform.yyconfig.openapi.api.ReleaseOpenApiService;
import com.yofish.platform.yyconfig.openapi.dto.NamespaceReleaseDTO;
import com.yofish.platform.yyconfig.openapi.dto.OpenReleaseDTO;
import com.yofish.yyconfig.common.common.utils.BeanUtils;
import com.yofish.yyconfig.common.common.utils.RequestPrecondition;
import com.yofish.yyconfig.common.common.utils.StringUtils;
import com.yofish.yyconfig.common.framework.apollo.core.enums.Env;
import com.youyu.common.exception.BizException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController("openapiReleaseController")
@RequestMapping("/openapi/v1/envs/{env}")
public class ReleaseController {

  private final ReleaseService releaseService;
  private final UserService userService;
  private final AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;
  private final ConsumerPermissionValidator consumerPermissionValidator;
  private final ReleaseOpenApiService releaseOpenApiService;

  public ReleaseController(
          final ReleaseService releaseService,
          final UserService userService,
          AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository, final ConsumerPermissionValidator consumerPermissionValidator,
          ReleaseOpenApiService releaseOpenApiService) {
    this.releaseService = releaseService;
    this.userService = userService;
    this.appEnvClusterNamespaceRepository = appEnvClusterNamespaceRepository;
    this.consumerPermissionValidator = consumerPermissionValidator;
    this.releaseOpenApiService = releaseOpenApiService;
  }

  @PreAuthorize(value = "@consumerPermissionValidator.hasReleaseNamespacePermission(#request, #appId, #namespaceName, #env)")
  @PostMapping(value = "/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/releases")
  public OpenReleaseDTO createRelease(@PathVariable String appId, @PathVariable String env,
                                      @PathVariable String clusterName,
                                      @PathVariable String namespaceName,
                                      @RequestBody NamespaceReleaseDTO model,
                                      HttpServletRequest request) {
    RequestPrecondition.checkArguments(!StringUtils.isContainEmpty(model.getReleasedBy(), model
            .getReleaseTitle()),
        "Params(releaseTitle and releasedBy) can not be empty");

    if (userService.findByUserId(model.getReleasedBy()) == null) {
      throw new BizException("user(releaseBy) not exists");
    }

    return this.releaseOpenApiService.publishNamespace(appId, env, clusterName, namespaceName, model);
  }

  @GetMapping(value = "/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/releases/latest")
  public OpenReleaseDTO loadLatestActiveRelease(@PathVariable String appId, @PathVariable String env,
                                                @PathVariable String clusterName, @PathVariable
                                                    String namespaceName) {
    return this.releaseOpenApiService.getLatestActiveRelease(appId, env, clusterName, namespaceName);
  }

    @PreAuthorize(value = "@consumerPermissionValidator.hasReleaseNamespacePermission(#request, #appId, #namespaceName, #env)")
    @PostMapping(value = "/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/branches/{branchName}/releases")
    public OpenReleaseDTO createGrayRelease(@PathVariable String appId,
                                        @PathVariable String env, @PathVariable String clusterName,
                                        @PathVariable String namespaceName, @PathVariable String branchName,
                                        @RequestBody NamespaceReleaseDTO model,
                                        HttpServletRequest request) {
        RequestPrecondition.checkArguments(!StringUtils.isContainEmpty(model.getReleasedBy(), model
                        .getReleaseTitle()),
                "Params(releaseTitle and releasedBy) can not be empty");

        if (userService.findByUserId(model.getReleasedBy()) == null) {
            throw new BizException("user(releaseBy) not exists");
        }

        NamespaceReleaseModel releaseModel = BeanUtils.transform(NamespaceReleaseModel.class, model);

//        releaseModel.setAppId(appId);
//        releaseModel.setEnv(Env.valueOf(env).toString());
//        releaseModel.setClusterName(branchName);
//        releaseModel.setNamespaceName(namespaceName);
      AppEnvClusterNamespace appEnvClusterNamespace = appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(appId, Env.valueOf(env).toString(), namespaceName, clusterName, AppEnvClusterNamespace.Type.Main.getValue());

      Release publish = releaseService.publish(appEnvClusterNamespace, releaseModel.getReleaseTitle(), releaseModel.getReleaseComment(), model.getReleasedBy(), model.isEmergencyPublish());

      return OpenApiBeanUtils.transformFromReleaseDTO(publish);
    }
//
//    @PreAuthorize(value = "@consumerPermissionValidator.hasReleaseNamespacePermission(#request, #appId, #namespaceName, #env)")
//    @PostMapping(value = "/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/branches/{branchName}/gray-del-releases")
//    public OpenReleaseDTO createGrayDelRelease(@PathVariable String appId,
//                                               @PathVariable String env, @PathVariable String clusterName,
//                                               @PathVariable String namespaceName, @PathVariable String branchName,
//                                               @RequestBody NamespaceGrayDelReleaseDTO model,
//                                               HttpServletRequest request) {
//        RequestPrecondition.checkArguments(!StringUtils.isContainEmpty(model.getReleasedBy(), model
//                        .getReleaseTitle()),
//                "Params(releaseTitle and releasedBy) can not be empty");
//        RequestPrecondition.checkArguments(model.getGrayDelKeys() != null,
//                "Params(grayDelKeys) can not be null");
//
//        if (userService.findByUserId(model.getReleasedBy()) == null) {
//            throw new BizException("user(releaseBy) not exists");
//        }
//
//        NamespaceGrayDelReleaseModel releaseModel = BeanUtils.transform(NamespaceGrayDelReleaseModel.class, model);
//        releaseModel.setAppId(appId);
//        releaseModel.setEnv(env.toUpperCase());
//        releaseModel.setClusterName(branchName);
//        releaseModel.setNamespaceName(namespaceName);
//
//        return OpenApiBeanUtils.transformFromReleaseDTO(releaseService.publish(releaseModel, releaseModel.getReleasedBy()));
//    }
//
//  @PutMapping(path = "/releases/{releaseId}/rollback")
//  public void rollback(@PathVariable String env,
//      @PathVariable long releaseId, @RequestParam String operator, HttpServletRequest request) {
//    RequestPrecondition.checkArguments(!StringUtils.isContainEmpty(operator),
//        "Param operator can not be empty");
//
//    if (userService.findByUserId(operator) == null) {
//      throw new BizException("user(operator) not exists");
//    }
//
//      releaseService.
//    ReleaseDTO release = releaseService.findReleaseById(Env.valueOf(env), releaseId);
//
//    if (release == null) {
//      throw new BizException("release not found");
//    }
//
//    if (!consumerPermissionValidator.hasReleaseNamespacePermission(request,release.getAppId(), release.getNamespaceName(), env)) {
//      throw new AccessDeniedException("Forbidden operation. you don't have release permission");
//    }
//
//    this.releaseOpenApiService.rollbackRelease(env, releaseId, operator);
//  }

}

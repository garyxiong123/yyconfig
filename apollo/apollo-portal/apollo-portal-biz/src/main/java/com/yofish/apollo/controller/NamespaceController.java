package com.yofish.apollo.controller;

import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.model.model.AppNamespaceModel;
import com.yofish.apollo.model.model.NamespaceCreationModel;
import com.yofish.apollo.service.AppNamespaceService;
import com.yofish.apollo.service.NamespaceService;
import com.youyu.common.api.Result;
import common.dto.NamespaceDTO;
import common.exception.BadRequestException;
import common.utils.BeanUtils;
import common.utils.InputValidator;
import common.utils.RequestPrecondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static common.utils.RequestPrecondition.checkModel;

//import com.yofish.apollo.model.bo.NamespaceBO;
//import com.yofish.apollo.model.model.NamespaceCreationModel;

@Slf4j
@RestController
public class NamespaceController {

    @Autowired
    private NamespaceService namespaceService;
    @Autowired
    private AppNamespaceService appNamespaceService;

    //  @Autowired
//  private RoleInitializationService roleInitializationService;
//  @Autowired
//  private RolePermissionService rolePermissionService;
//  @Autowired
//  private PortalConfig portalConfig;
//  @Autowired
//  private PermissionValidator permissionValidator;
//
//
//  @RequestMapping(value = "/appnamespaces/public", method = RequestMethod.GET)
//  public List<AppNamespace> findPublicAppNamespaces() {
//    return appNamespaceService.findPublicAppNamespaces();
//  }
//
//  @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces", method = RequestMethod.GET)
//  public List<NamespaceBO> findNamespaces(@PathVariable String appId, @PathVariable String env,
//                                          @PathVariable String clusterName) {
//
//    List<NamespaceBO> namespaceBOs = namespaceService.findNamespaceBOs(appId, Env.valueOf(env), clusterName);
//
//    for (NamespaceBO namespaceBO : namespaceBOs) {
//      if (permissionValidator.shouldHideConfigToCurrentUser(appId, env, namespaceBO.getBaseInfo().getNamespaceName())) {
//        namespaceBO.hideItems();
//      }
//    }
//
//    return namespaceBOs;
//  }
//
//  @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName:.+}", method = RequestMethod.GET)
//  public NamespaceBO findNamespace(@PathVariable String appId, @PathVariable String env,
//                                   @PathVariable String clusterName, @PathVariable String namespaceName) {
//
//    NamespaceBO namespaceBO = namespaceService.loadNamespaceBO(appId, Env.valueOf(env), clusterName, namespaceName);
//
//    if (namespaceBO != null && permissionValidator.shouldHideConfigToCurrentUser(appId, env, namespaceName)) {
//      namespaceBO.hideItems();
//    }
//
//    return namespaceBO;
//  }
//
//  @RequestMapping(value = "/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/associated-public-namespace",
//      method = RequestMethod.GET)
//  public NamespaceBO findPublicNamespaceForAssociatedNamespace(@PathVariable String env,
//                                                               @PathVariable String appId,
//                                                               @PathVariable String namespaceName,
//                                                               @PathVariable String clusterName) {
//
//    return namespaceService.findPublicNamespaceForAssociatedNamespaceToBo(Env.valueOf(env), appId, clusterName, namespaceName);
//  }
//
//  @PreAuthorize(value = "@permissionValidator.hasCreateNamespacePermission(#appId)")
    @RequestMapping(value = "/apps/{appId}/namespaces", method = RequestMethod.POST)
    public Result createNamespace(@PathVariable String appId,
                                  @RequestBody List<NamespaceCreationModel> models) {

        checkModel(!CollectionUtils.isEmpty(models));

        for (NamespaceCreationModel model : models) {
            NamespaceDTO namespace = model.getNamespace();
            RequestPrecondition.checkArgumentsNotEmpty(model.getEnv(), namespace.getAppId(),
                    namespace.getClusterName(), namespace.getNamespaceName());

            try {
                namespaceService.createNamespace(model.getEnv(), namespace);
            } catch (Exception e) {
                log.error("create namespace fail.", e);
            }
        }

        return Result.ok();
    }

    //
//  @PreAuthorize(value = "@permissionValidator.hasDeleteNamespacePermission(#appId)")
//  @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName:.+}", method = RequestMethod.DELETE)
//  public ResponseEntity<Void> deleteNamespace(@PathVariable String appId, @PathVariable String env,
//                                              @PathVariable String clusterName, @PathVariable String namespaceName) {
//
//    namespaceService.deleteNamespace(appId, Env.valueOf(env), clusterName, namespaceName);
//
//    return ResponseEntity.ok().build();
//  }
//
//  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
//  @RequestMapping(value = "/apps/{appId}/appnamespaces/{namespaceName:.+}", method = RequestMethod.DELETE)
//  public ResponseEntity<Void> deleteAppNamespace(@PathVariable String appId, @PathVariable String namespaceName) {
//
//    AppNamespace appNamespace = appNamespaceService.deleteAppNamespace(appId, namespaceName);
//
//    publisher.publishEvent(new AppNamespaceDeletionEvent(appNamespace));
//
//    return ResponseEntity.ok().build();
//  }
//
//  @RequestMapping(value = "/apps/{appId}/appnamespaces/{namespaceName:.+}", method = RequestMethod.GET)
//  public AppNamespaceDTO findAppNamespace(@PathVariable String appId, @PathVariable String namespaceName) {
//    AppNamespace appNamespace = appNamespaceService.findByAppIdAndName(appId, namespaceName);
//
//    if (appNamespace == null) {
//      throw new BadRequestException(
//          String.format("AppNamespace not exists. AppId = %s, NamespaceName = %s", appId, namespaceName));
//    }
//
//    return BeanUtils.transform(AppNamespaceDTO.class, appNamespace);
//  }
//
//  @PreAuthorize(value = "@permissionValidator.hasCreateAppNamespacePermission(#appId, #appNamespace)")
    @RequestMapping(value = "/apps/{appId}/appnamespaces", method = RequestMethod.POST)
    public AppNamespace createAppNamespace(@PathVariable String appId,
                                           @RequestParam(defaultValue = "true") boolean appendNamespacePrefix,
                                           @RequestBody AppNamespaceModel appNamespaceModel) {

        RequestPrecondition.checkArgumentsNotEmpty(appNamespaceModel.getAppId(), appNamespaceModel.getName());
        if (!InputValidator.isValidAppNamespace(appNamespaceModel.getName())) {
            throw new BadRequestException(String.format("Namespace格式错误: %s",
                    InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE + " & "
                            + InputValidator.INVALID_NAMESPACE_NAMESPACE_MESSAGE));
        }
        AppNamespace appNamespace = BeanUtils.transform(AppNamespace.class, appNamespaceModel);
        AppNamespace createdAppNamespace = appNamespaceService.createAppNamespace(appNamespace, appendNamespacePrefix);

//    if (portalConfig.canAppAdminCreatePrivateNamespace() || createdAppNamespace.isPublic()) {
//      assignNamespaceRoleToOperator(appId, appNamespaceModel.getName());
//    }

//    publisher.publishEvent(new AppNamespaceCreationEvent(createdAppNamespace));

        return createdAppNamespace;
    }


//
//  *
//   * env -> cluster -> cluster has not published namespace?
//   * Example:
//   * dev ->
//   *  default -> true   (default cluster has not published namespace)
//   *  customCluster -> false (customCluster cluster's all namespaces had published)
//
//  @RequestMapping(value = "/apps/{appId}/namespaces/publish_info", method = RequestMethod.GET)
//  public Map<String, Map<String, Boolean>> getNamespacesPublishInfo(@PathVariable String appId) {
//    return namespaceService.getNamespacesPublishInfo(appId);
//  }
//
//  @RequestMapping(value = "/envs/{env}/appnamespaces/{publicNamespaceName}/namespaces", method = RequestMethod.GET)
//  public List<Namespace> getPublicAppNamespaceAllNamespaces(@PathVariable String env,
//                                                               @PathVariable String publicNamespaceName,
//                                                               @RequestParam(name = "page", defaultValue = "0") int page,
//                                                               @RequestParam(name = "size", defaultValue = "10") int size) {
//
//    return namespaceService.getPublicAppNamespaceAllNamespaces(Env.fromString(env), publicNamespaceName, page, size);
//
//  }
//
//  private void assignNamespaceRoleToOperator(String appId, String namespaceName) {
//    //default assign modify、release namespace role to namespace creator
//    String operator = userInfoHolder.getUser().getUserId();
//
//    rolePermissionService
//        .assignRoleToUsers(RoleUtils.buildNamespaceRoleName(appId, namespaceName, RoleType.MODIFY_NAMESPACE),
//                           Sets.newHashSet(operator), operator);
//    rolePermissionService
//        .assignRoleToUsers(RoleUtils.buildNamespaceRoleName(appId, namespaceName, RoleType.RELEASE_NAMESPACE),
//                           Sets.newHashSet(operator), operator);
//  }
}

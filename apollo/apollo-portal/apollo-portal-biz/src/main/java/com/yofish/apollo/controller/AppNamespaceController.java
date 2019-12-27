package com.yofish.apollo.controller;

import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.NamespaceListReq;
import com.yofish.apollo.dto.NamespaceListResp;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.yofish.apollo.model.model.AppNamespaceModel;
import com.yofish.apollo.service.AppEnvClusterNamespaceService;
import com.yofish.apollo.service.AppNamespaceService;
import com.youyu.common.api.Result;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.youyu.common.utils.YyAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@Slf4j
@RestController
@Api(description = "项目命名空间")
public class AppNamespaceController {

    @Autowired
    private AppNamespaceService appNamespaceService;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;

    @ApiOperation("创建项目私有命名空间")
    @PostMapping("/apps/{appId:\\d+}/namespaces/private")
    public Result<AppNamespace4Private> createAppPrivateNamespace(@PathVariable long appId, @Valid @RequestBody AppNamespaceModel model) {

        AppNamespace4Private appNamespace4Private = AppNamespace4Private.builder()
                .app(new App(appId))
                .name(model.getName())
                .format(model.getFormat())
                .comment(model.getComment())
                .build();

        appNamespace4Private = appNamespaceService.createAppNamespace(appNamespace4Private);

        return Result.ok(appNamespace4Private);
    }

    @ApiOperation("创建项目受保护命名空间")
    @PostMapping("/apps/{appId:\\d+}/namespaces/protect")
    public Result<AppNamespace4Protect> createAppProtectNamespace(@PathVariable long appId, @Valid @RequestBody AppNamespaceModel model) {
        AppNamespace4Protect appNamespace4Protect = AppNamespace4Protect.builder()
                .app(new App(appId))
                .name(model.getName())
                .authorizedApp(model.getAuthorizedApp())
                .format(model.getFormat())
                .comment(model.getComment())
                .openNamespaceType(ObjectUtils.isEmpty(model.getOpenNamespaceTypeId()) ? null : new OpenNamespaceType(model.getOpenNamespaceTypeId()))
                .build();

        appNamespace4Protect = appNamespaceService.createAppNamespace(appNamespace4Protect);

        return Result.ok(appNamespace4Protect);
    }

    @ApiOperation("创建项目公开命名空间")
    @PostMapping("/apps/{appId:\\d+}/namespaces/public")
    public Result<AppNamespace4Public> createAppPublicNamespace(@PathVariable long appId, @Valid @RequestBody AppNamespaceModel model) {
        AppNamespace4Public appNamespace4Public = AppNamespace4Public.builder()
                .app(new App(appId))
                .name(model.getName())
                .format(model.getFormat())
                .comment(model.getComment())
                .openNamespaceType(ObjectUtils.isEmpty(model.getOpenNamespaceTypeId()) ? null : new OpenNamespaceType(model.getOpenNamespaceTypeId()))
                .build();

        appNamespace4Public = appNamespaceService.createAppNamespace(appNamespace4Public);

        return Result.ok(appNamespace4Public);
    }

    @ApiOperation("查询项目的命名空间")
    @GetMapping("/apps/{appId:\\d+}/namespaces/{namespace:[0-9a-zA-Z_.-]+}")
    public Result<AppNamespace> getAppNamespaceInfo(@PathVariable long appId, @PathVariable String namespace) {
        AppNamespace byAppIdAndName = appNamespaceService.findByAppIdAndName(appId, namespace);
        return Result.ok(byAppIdAndName);
    }

    @ApiOperation("项目受保护命名空间授权")
    @PostMapping("/apps/{appId:\\d+}/namespaces/{namespace:[0-9a-zA-Z_.-]+}/authorize")
    public Result<AppNamespace4Protect> authorizedApp(@PathVariable long appId, @PathVariable String namespace, @RequestBody Set<App> apps) {
        AppNamespace4Protect appNamespace4Protect = appNamespaceService.findProtectAppNamespaceByAppIdAndName(appId, namespace);
        if (ObjectUtils.isEmpty(appNamespace4Protect)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "app namespace not exist!");
        }
        appNamespace4Protect.setAuthorizedApp(apps);
        appNamespaceService.updateAppNamespace(appNamespace4Protect);

        return Result.ok(appNamespace4Protect);
    }

    @ApiOperation("创建关联公开命名空间")
    @PostMapping("/apps/{appId:\\d+}/namespaces/{namespaceId:\\d+}/associate/{appEnvClusterIds:[0-9,]+}")
    public Result createRelationNamespace(@PathVariable Long appId, @PathVariable Long namespaceId, @PathVariable String appEnvClusterIds) {
        AppNamespace4Public appPublicNamespace = this.appNamespaceService.findAppPublicNamespace(namespaceId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appPublicNamespace), "关联的公共命名空间不存在！");
        YyAssert.paramCheck(appId.equals(appPublicNamespace.getApp().getId()), "不能关联自己的公共命名空间！");

        Arrays.stream(appEnvClusterIds.split(",")).forEach(appEnvClusterId -> {
            AppEnvClusterNamespace4Main appEnvClusterNamespace = new AppEnvClusterNamespace4Main(new AppEnvCluster(Long.valueOf(appEnvClusterId)), new AppNamespace4Public(namespaceId));
            this.appEnvClusterNamespaceService.save(appEnvClusterNamespace);
        });
        return Result.ok();
    }

    @ApiOperation("项目环境集群下的所有命名空间配置信息")
    @GetMapping("/apps/{appCode}/envs/{env}/clusters/{clusterName}/namespaces")
    public Result<List<NamespaceVO>> findNamespaces(@PathVariable String appCode, @PathVariable String env,
                                                    @PathVariable String clusterName) {
        List<NamespaceVO> namespaceVOs = this.appEnvClusterNamespaceService.findNamespaceVOs(appCode, env, clusterName);
        return Result.ok(namespaceVOs);
    }

    @ApiOperation("查询关联的公共命名空间")
    @GetMapping("/envs/{env}/apps/{appCode}/clusters/{clusterName}/namespaces/{namespaceName}/associated-public-namespace")
    public Result<NamespaceVO> findPublicNamespaceForAssociatedNamespace(@PathVariable String env,
                                                                         @PathVariable String appCode,
                                                                         @PathVariable String namespaceName,
                                                                         @PathVariable String clusterName) {
        NamespaceVO publicNamespaceVoForAssociatedNamespace = appEnvClusterNamespaceService.findPublicNamespaceVoForAssociatedNamespace(env, clusterName, namespaceName);

        return Result.ok(publicNamespaceVoForAssociatedNamespace);
    }


    @ApiOperation("查询所有的公共命名空间")
    @GetMapping("/app/namespaces/public")
    public Result<List<AppNamespace4Public>> findPublicAppNamespaces() {
        return Result.ok(appNamespaceService.findAllPublicAppNamespace());
    }


    @ApiOperation("查询所有app下环境集群附带id")
    @PostMapping("/namespaceList")
    public Result<List<NamespaceListResp>> namespaceList(@RequestBody NamespaceListReq namespaceListReq) {
        List<AppEnvClusterNamespace> listResps = appEnvClusterNamespaceService.findbyAppAndEnvAndNamespace(namespaceListReq.getAppCode(), namespaceListReq.getNamespace());
        List<NamespaceListResp> respVos = new ArrayList<>();
        if (listResps != null && listResps.size() > 0) {

            for (AppEnvClusterNamespace item : listResps) {
                NamespaceListResp respvo = new NamespaceListResp();
                respvo.setEnv(item.getAppEnvCluster().getEnv());
                respvo.setName(item.getAppEnvCluster().getName());
                respvo.setId(item.getId());
                respVos.add(respvo);
            }
        }
        return Result.ok(respVos);

    }
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
//  @RequestMapping(value = "/envs/{env}/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/associated-public-appNamespace",
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
/*    @RequestMapping(value = "/apps/{appId}/namespaces", method = RequestMethod.POST)
    public Result createNamespace(@PathVariable String appId,
                                  @RequestBody List<NamespaceCreationModel> models) {

        checkModel(!CollectionUtils.isEmpty(models));

        for (NamespaceCreationModel model : models) {
            NamespaceDTO appNamespace = model.getAppNamespace();
            RequestPrecondition.checkArgumentsNotEmpty(model.getEnv(), appNamespace.getAppId(),
                    appNamespace.getClusterName(), appNamespace.getNamespaceName());

            try {
                appEnvClusterNamespaceService.createNamespace(model.getEnv(), appNamespace);
            } catch (Exception e) {
                log.error("create appNamespace fail.", e);
            }
        }

        return Result.ok();
    }*/

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
//  public AppNamespaceDTO findAppPublicNamespace(@PathVariable String appId, @PathVariable String namespaceName) {
//    AppNamespace appNamespace = appNamespaceService.findByAppIdAndName(appId, namespaceName);
//
//    if (appNamespace == null) {
//      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG,
//          String.format("AppNamespace not exists. AppId = %s, NamespaceName = %s", appId, namespaceName));
//    }
//
//    return BeanUtils.transform(AppNamespaceDTO.class, appNamespace);
//  }
//
//
//  *
//   * env -> appEnvCluster -> appEnvCluster has not published appNamespace?
//   * Example:
//   * dev ->
//   *  default -> true   (default appEnvCluster has not published appNamespace)
//   *  customCluster -> false (customCluster appEnvCluster's all namespaces had published)
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
//    //default assign modify、release appNamespace role to appNamespace creator
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

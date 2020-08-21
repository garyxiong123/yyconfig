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

import com.yofish.apollo.domain.*;
import com.yofish.apollo.api.dto.NamespaceEnvTree;
import com.yofish.apollo.api.dto.NamespaceListReq;
import com.yofish.apollo.api.dto.NamespaceListResp;
import com.yofish.apollo.api.dto.PublicProtectNamespaceDto;
import com.yofish.apollo.enums.AppNamespaceType;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.yofish.apollo.model.AppNamespaceModel;
import com.yofish.apollo.service.AppEnvClusterNamespaceService;
import com.yofish.apollo.service.AppNamespaceService;
import com.yofish.apollo.service.AppService;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Slf4j
@RestController
@Api(description = "项目命名空间")
public class AppNamespaceController {
    @Autowired
    private AppService appService;
    @Autowired
    private AppNamespaceService appNamespaceService;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;

    @ApiOperation("创建项目私有命名空间")
    @PostMapping("/apps/{appId:\\d+}/namespaces/private")
    public Result<AppNamespace> createAppPrivateNamespace(@PathVariable long appId, @Valid @RequestBody AppNamespaceModel model) {

        model.setAppId(appId);
        model.setAppNamespaceType(AppNamespaceType.Private);
        AppNamespace appNamespace4Private = appNamespaceService.createAppNamespace(model);

        return Result.ok(appNamespace4Private);
    }

    @ApiOperation("创建项目受保护命名空间")
    @PostMapping("/apps/{appId:\\d+}/namespaces/protect")
    public Result<AppNamespace> createAppProtectNamespace(@PathVariable long appId, @Valid @RequestBody AppNamespaceModel model) {
        model.setAppId(appId);
        model.setAppNamespaceType(AppNamespaceType.Protect);
        AppNamespace appNamespace4Protect = appNamespaceService.createAppNamespace(model);
        return Result.ok(appNamespace4Protect);
    }

    @ApiOperation("创建项目公开命名空间")
    @PostMapping("/apps/{appId:\\d+}/namespaces/public")
    public Result<AppNamespace> createAppPublicNamespace(@PathVariable long appId, @Valid @RequestBody AppNamespaceModel model) {
        model.setAppId(appId);
        model.setAppNamespaceType(AppNamespaceType.Public);
        AppNamespace appNamespace4Public = appNamespaceService.createAppNamespace(model);

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
    public Result<AppNamespace> authorizedApp(@PathVariable long appId, @PathVariable String namespace, @RequestBody Set<App> apps) {
        AppNamespace appNamespace4Protect = appNamespaceService.findProtectAppNamespaceByAppIdAndName(appId, namespace);
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
        AppNamespace appNamespace = this.appNamespaceService.findAppNamespace(namespaceId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appNamespace), "关联的公共命名空间不存在！");
        YyAssert.paramCheck(appId.equals(appNamespace.getApp().getId()), "不能关联自己的公共命名空间！");

        Arrays.stream(appEnvClusterIds.split(",")).forEach(appEnvClusterId -> {
            AppEnvClusterNamespace4Main appEnvClusterNamespace = new AppEnvClusterNamespace4Main(new AppEnvCluster(Long.valueOf(appEnvClusterId)), appNamespace);
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
    public Result<List<AppNamespace>> findPublicAppNamespaces() {
        return Result.ok(appNamespaceService.findAllPublicAppNamespace());
    }

    @ApiOperation("查询项目拥有授权的protect命名空间")
    @GetMapping("/app/{appCode}/namespaces/protect/authorized")
    public Result<List<AppNamespace>> findProtectAppNamespaces(@PathVariable String appCode) {
        return Result.ok(appNamespaceService.findAllProtectAppNamespaceByAuthorized(appCode));
    }

    @ApiOperation("查询所有的公共命名空间和项目拥有授权的protect命名空间")
    @GetMapping("/app/{appCode}/namespaces/publicAndProtect")
    public Result<PublicProtectNamespaceDto> findPublicAppNamespacesAndProtectAppNamespaces(@PathVariable String appCode) {
        PublicProtectNamespaceDto allPublicAndAuthorizedNamespace = appNamespaceService.findAllPublicAndAuthorizedNamespace(appCode);
        return Result.ok(allPublicAndAuthorizedNamespace);
    }


    @ApiOperation("查询所有app下环境集群附带id")
    @PostMapping("/namespaceList")
    public Result<List<NamespaceEnvTree>> namespaceList(@RequestBody NamespaceListReq namespaceListReq) {
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
        List<NamespaceEnvTree> trees = tansTotree(respVos);
        return Result.ok(trees);

    }

    private List<NamespaceEnvTree> tansTotree(List<NamespaceListResp> listResps) {
        List<NamespaceEnvTree> namespaceEnvTreeList = new ArrayList<>();
        Map<String, List<NamespaceListResp>> nameTrees = new HashMap<>(4);
        nameTrees = listResps.stream().sorted(Comparator.comparing(NamespaceListResp::getEnv)).collect(Collectors.groupingBy(m -> m.getEnv()));
        for (String itemKey : nameTrees.keySet()) {
            NamespaceEnvTree namespaceEnvTree = new NamespaceEnvTree();
            namespaceEnvTree.setEnv(itemKey);
            namespaceEnvTree.setNamespaceListResps(nameTrees.get(itemKey));
            namespaceEnvTreeList.add(namespaceEnvTree);
        }
        return namespaceEnvTreeList;
    }

}

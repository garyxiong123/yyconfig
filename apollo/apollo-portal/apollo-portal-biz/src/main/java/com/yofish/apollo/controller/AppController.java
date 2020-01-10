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


import com.yofish.apollo.component.AppPreAuthorize;
import com.yofish.apollo.domain.App;
import com.yofish.apollo.model.model.AppModel;
import com.yofish.apollo.model.vo.EnvClusterInfo;
import com.yofish.apollo.service.AppService;
import com.yofish.apollo.service.ServerConfigService;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import common.utils.InputValidator;
import common.utils.RequestPrecondition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.youyu.common.api.Result.ok;

@Api(description = "项目")
@RestController
@RequestMapping("/apps")
public class AppController {

    @Autowired
    private AppService appService;
    @Autowired
    private ServerConfigService serverConfigService;


    /**
     * create
     * <p>
     * hasCreateApplicationPermission
     *
     * @param appModel
     * @return
     */
    @PostMapping
    @ApiOperation("创建项目")
    @AppPreAuthorize(AppPreAuthorize.Authorize.SuperAdmin)
    public Result<App> create(@Valid @RequestBody AppModel appModel) {

        App app = transformToApp(appModel);

        appService.createApp(app);

        return ok(app);
    }


    @GetMapping("/search")
    @ApiOperation("查询项目")
    public Result<PageData<App>> searchByAppCodeOrAppName(@RequestParam(required = false) String query, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (StringUtils.isEmpty(query)) {
            return ok(appService.findAllWithAuthorize(pageable));
        } else {
            return ok(appService.searchByAppCodeOrAppName(query, pageable));
        }
    }

    @GetMapping
    @ApiOperation("查询所有的项目")
    public Result<List<App>> getAllAppWithAuthorize() {
        List<App> all = appService.findAllWithAuthorize();
        return ok(all);
    }

    @GetMapping("/{appId:\\d+}")
    @ApiOperation("查询项目信息")
    public Result<App> getApp(@PathVariable Long appId) {
        App app = appService.getApp(appId);
        return ok(app);
    }


    @PutMapping("/{appId:\\d+}")
    @ApiOperation("修改项目信息")
    @AppPreAuthorize(AppPreAuthorize.Authorize.AppOwner)
    public Result<App> update(@PathVariable Long appId, @Valid @RequestBody AppModel appModel) {
        App app = transformToApp(appModel);
        app.setId(appId);

        App updatedApp = appService.updateApp(app);
        return ok(updatedApp);
    }

    @GetMapping("/code/{appCode:[0-9a-zA-Z_.-]+}")
    @ApiOperation("查询项目信息")
    public Result<App> getAppByCode(@PathVariable String appCode) {
        App app = appService.getApp(appCode);
        if (app == null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "项目不存在！");
        }
        return ok(app);
    }

    @ApiOperation("项目菜单")
    @GetMapping("/{appId:\\d+}/navtree")
    public Result<List<EnvClusterInfo>> nav(@PathVariable long appId) {
        List<EnvClusterInfo> envClusterInfoList = new ArrayList<>();
        List<String> envs = this.serverConfigService.getActiveEnvs();
        if(CollectionUtils.isEmpty(envs)){
          return ok(null);
        }
        for (String env : envs) {
            envClusterInfoList.add(appService.createEnvNavNode(env, appId));
        }
        return ok(envClusterInfoList);
    }


    private App transformToApp(AppModel appModel) {
        String appCode = appModel.getAppCode();
        String appName = appModel.getName();
        Long ownerId = appModel.getOwnerId();
        Long orgId = appModel.getOrgId();
        Set<Long> admins = appModel.getAdmins();

        RequestPrecondition.checkArgumentsNotEmpty(appCode, appName, ownerId, orgId);

        if (!InputValidator.isValidClusterNamespace(appModel.getAppCode())) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG,
                    String.format("AppCode格式错误: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
        }
        return App.builder()
                .appCode(appCode)
                .name(appName)
                .department(new Department(orgId))
                .appOwner(new User(ownerId))
                .appAdmins(ObjectUtils.isEmpty(admins) ? null : admins.stream().map(userId -> new User(userId)).collect(Collectors.toSet()))
                .build();

    }
}

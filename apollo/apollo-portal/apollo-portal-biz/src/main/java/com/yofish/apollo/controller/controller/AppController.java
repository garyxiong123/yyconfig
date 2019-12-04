package com.yofish.apollo.controller.controller;


import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Department;
import com.yofish.apollo.model.AppModel;
import com.yofish.apollo.service.AppService;
import com.yofish.gary.biz.domain.User;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import common.exception.BadRequestException;
import common.utils.InputValidator;
import common.utils.RequestPrecondition;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/apps")
public class AppController {

    @Autowired
    private AppService appService;


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
    public Result<App> create(@Valid @RequestBody AppModel appModel) {

        App app = transformToApp(appModel);

        App createdApp = appService.createAppInLocal(app);

    /*Set<String> admins = appModel.getAdmins();
    if (!CollectionUtils.isEmpty(admins)) {
      rolePermissionService
              .assignRoleToUsers(RoleUtils.buildAppMasterRoleName(createdApp.getAppCode()),
                      admins, YyRequestInfoHelper.getCurrentUserId());
    }*/

        return Result.ok(createdApp);
    }


    @GetMapping("/search")
    @ApiOperation("查询")
    public PageData<App> searchByAppCodeOrAppName(@RequestParam(required = false) String query, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (StringUtils.isEmpty(query)) {
            return appService.findAll(pageable);
        } else {
            return appService.searchByAppCodeOrAppName(query, pageable);
        }
    }

    private App transformToApp(AppModel appModel) {
        String appCode = appModel.getAppCode();
        String appName = appModel.getName();
        Long ownerId = appModel.getOwnerId();
        Long orgId = appModel.getOrgId();
        Set<Long> admins = appModel.getAdmins();

        RequestPrecondition.checkArgumentsNotEmpty(appCode, appName, ownerId, orgId);

        if (!InputValidator.isValidClusterNamespace(appModel.getAppCode())) {
            throw new BadRequestException(
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

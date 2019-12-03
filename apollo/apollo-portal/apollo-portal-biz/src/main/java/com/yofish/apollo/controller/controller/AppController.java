package com.yofish.apollo.controller.controller;


import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Department;
import com.yofish.apollo.model.AppModel;
import com.yofish.apollo.service.AppService;
import com.yofish.apollo.util.RoleUtils;
import com.yofish.gary.biz.domain.User;
import com.youyu.common.helper.YyRequestInfoHelper;
import common.exception.BadRequestException;
import common.utils.InputValidator;
import common.utils.RequestPrecondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
   *
   * hasCreateApplicationPermission
   *
   * @param appModel
   * @return
   */
  @PostMapping
  public App create(@Valid @RequestBody AppModel appModel) {

    App app = transformToApp(appModel);

    App createdApp = appService.createAppInLocal(app);

    /*Set<String> admins = appModel.getAdmins();
    if (!CollectionUtils.isEmpty(admins)) {
      rolePermissionService
              .assignRoleToUsers(RoleUtils.buildAppMasterRoleName(createdApp.getAppCode()),
                      admins, YyRequestInfoHelper.getCurrentUserId());
    }*/

    return createdApp;
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
            .appAdmins(ObjectUtils.isEmpty(admins)?null:admins.stream().map(userId->new User(userId)).collect(Collectors.toSet()))
            .build();

  }
}

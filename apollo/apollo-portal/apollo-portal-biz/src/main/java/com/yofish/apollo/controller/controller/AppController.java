package com.yofish.apollo.controller.controller;


import com.yofish.apollo.domain.App;
import com.yofish.apollo.model.AppModel;
import com.yofish.apollo.service.AppService;
import com.yofish.apollo.util.RoleUtils;
import com.youyu.common.helper.YyRequestInfoHelper;
import common.exception.BadRequestException;
import common.utils.InputValidator;
import common.utils.RequestPrecondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;


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
    String appId = appModel.getAppId();
    String appName = appModel.getName();
    String ownerName = appModel.getOwnerName();
    String orgId = appModel.getOrgId();
    String orgName = appModel.getOrgName();

    RequestPrecondition.checkArgumentsNotEmpty(appId, appName, ownerName, orgId, orgName);

    if (!InputValidator.isValidClusterNamespace(appModel.getAppId())) {
      throw new BadRequestException(
          String.format("AppId格式错误: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
    }
    return App.builder()
//        .appId(appId)
        .name(appName)
//            .ownerName(ownerName)
//            .orgId(orgId)
//            .orgName(orgName)
        .build();

  }
}

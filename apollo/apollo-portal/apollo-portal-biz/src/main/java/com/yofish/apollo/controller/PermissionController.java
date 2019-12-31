package com.yofish.apollo.controller;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/*
@RestController
public class PermissionController {

  @Autowired
  private UserInfoHolder userInfoHolder;
  @Autowired
  private RolePermissionService rolePermissionService;
  @Autowired
  private UserService userService;
  @Autowired
  private RoleInitializationService roleInitializationService;

  @RequestMapping(value = "/apps/{appCode}/initPermission", method = RequestMethod.POST)
  public ResponseEntity<Void> initAppPermission(@PathVariable String appCode, @RequestBody String namespaceName) {
    roleInitializationService.initNamespaceEnvRoles(appCode, namespaceName, userInfoHolder.getUser().getUserId());
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/apps/{appCode}/permissions/{permissionType}", method = RequestMethod.GET)
  public ResponseEntity<PermissionCondition> hasPermission(@PathVariable String appCode, @PathVariable String permissionType) {
    PermissionCondition permissionCondition = new PermissionCondition();

    permissionCondition.setHasPermission(
        rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(), permissionType, appCode));

    return ResponseEntity.ok().body(permissionCondition);
  }

  @RequestMapping(value = "/apps/{appCode}/namespaces/{namespaceName}/permissions/{permissionType}", method = RequestMethod.GET)
  public ResponseEntity<PermissionCondition> hasPermission(@PathVariable String appCode, @PathVariable String namespaceName,
                                                           @PathVariable String permissionType) {
    PermissionCondition permissionCondition = new PermissionCondition();

    permissionCondition.setHasPermission(
        rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(), permissionType,
            RoleUtils.buildNamespaceTargetId(appCode, namespaceName)));

    return ResponseEntity.ok().body(permissionCondition);
  }

  @RequestMapping(value = "/apps/{appCode}/envs/{env}/namespaces/{namespaceName}/permissions/{permissionType}", method = RequestMethod.GET)
  public ResponseEntity<PermissionCondition> hasPermission(@PathVariable String appCode, @PathVariable String env, @PathVariable String namespaceName,
                                                           @PathVariable String permissionType) {
    PermissionCondition permissionCondition = new PermissionCondition();

    permissionCondition.setHasPermission(
        rolePermissionService.userHasPermission(userInfoHolder.getUser().getUserId(), permissionType,
            RoleUtils.buildNamespaceTargetId(appCode, namespaceName, env)));

    return ResponseEntity.ok().body(permissionCondition);
  }

  @RequestMapping(value = "/permissions/root", method = RequestMethod.GET)
  public ResponseEntity<PermissionCondition> hasRootPermission() {
    PermissionCondition permissionCondition = new PermissionCondition();

    permissionCondition.setHasPermission(rolePermissionService.isSuperAdmin(userInfoHolder.getUser().getUserId()));

    return ResponseEntity.ok().body(permissionCondition);
  }


  @RequestMapping(value = "/apps/{appCode}/envs/{env}/namespaces/{namespaceName}/role_users", method = RequestMethod.GET)
  public NamespaceEnvRolesAssignedUsers getNamespaceEnvRoles(@PathVariable String appCode, @PathVariable String env, @PathVariable String namespaceName) {

    // validate env parameter
    if (Env.UNKNOWN == EnvUtils.transformEnv(env)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "env is illegal");
    }

    NamespaceEnvRolesAssignedUsers assignedUsers = new NamespaceEnvRolesAssignedUsers();
    assignedUsers.setNamespaceName(namespaceName);
    assignedUsers.setAppId(appCode);
    assignedUsers.setEnv(Env.fromString(env));

    Set<UserInfo> releaseNamespaceUsers =
        rolePermissionService.queryUsersWithRole(RoleUtils.buildReleaseNamespaceRoleName(appCode, namespaceName, env));
    assignedUsers.setReleaseRoleUsers(releaseNamespaceUsers);

    Set<UserInfo> modifyNamespaceUsers =
        rolePermissionService.queryUsersWithRole(RoleUtils.buildModifyNamespaceRoleName(appCode, namespaceName, env));
    assignedUsers.setModifyRoleUsers(modifyNamespaceUsers);

    return assignedUsers;
  }

  @PreAuthorize(value = "@permissionValidator.hasAssignRolePermission(#appCode)")
  @RequestMapping(value = "/apps/{appCode}/envs/{env}/namespaces/{namespaceName}/roles/{roleType}", method = RequestMethod.POST)
  public ResponseEntity<Void> assignNamespaceEnvRoleToUser(@PathVariable String appCode, @PathVariable String env, @PathVariable String namespaceName,
                                                           @PathVariable String roleType, @RequestBody String user) {
    checkUserExists(user);
    RequestPrecondition.checkArgumentsNotEmpty(user);

    if (!RoleType.isValidRoleType(roleType)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "role type is illegal");
    }

    // validate env parameter
    if (Env.UNKNOWN == EnvUtils.transformEnv(env)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "env is illegal");
    }
    Set<String> assignedUser = rolePermissionService.assignRoleToUsers(RoleUtils.buildNamespaceRoleName(appCode, namespaceName, roleType, env),
        Sets.newHashSet(user), userInfoHolder.getUser().getUserId());
    if (CollectionUtils.isEmpty(assignedUser)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, user + "已授权");
    }

    return ResponseEntity.ok().build();
  }

  @PreAuthorize(value = "@permissionValidator.hasAssignRolePermission(#appCode)")
  @RequestMapping(value = "/apps/{appCode}/envs/{env}/namespaces/{namespaceName}/roles/{roleType}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> removeNamespaceEnvRoleFromUser(@PathVariable String appCode, @PathVariable String env, @PathVariable String namespaceName,
                                                             @PathVariable String roleType, @RequestParam String user) {
    RequestPrecondition.checkArgumentsNotEmpty(user);

    if (!RoleType.isValidRoleType(roleType)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "role type is illegal");
    }
    // validate env parameter
    if (Env.UNKNOWN == EnvUtils.transformEnv(env)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "env is illegal");
    }
    rolePermissionService.removeRoleFromUsers(RoleUtils.buildNamespaceRoleName(appCode, namespaceName, roleType, env),
        Sets.newHashSet(user), userInfoHolder.getUser().getUserId());
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/apps/{appCode}/namespaces/{namespaceName}/role_users", method = RequestMethod.GET)
  public NamespaceRolesAssignedUsers getNamespaceRoles(@PathVariable String appCode, @PathVariable String namespaceName) {

    NamespaceRolesAssignedUsers assignedUsers = new NamespaceRolesAssignedUsers();
    assignedUsers.setNamespaceName(namespaceName);
    assignedUsers.setAppId(appCode);

    Set<UserInfo> releaseNamespaceUsers =
        rolePermissionService.queryUsersWithRole(RoleUtils.buildReleaseNamespaceRoleName(appCode, namespaceName));
    assignedUsers.setReleaseRoleUsers(releaseNamespaceUsers);

    Set<UserInfo> modifyNamespaceUsers =
        rolePermissionService.queryUsersWithRole(RoleUtils.buildModifyNamespaceRoleName(appCode, namespaceName));
    assignedUsers.setModifyRoleUsers(modifyNamespaceUsers);

    return assignedUsers;
  }

  @PreAuthorize(value = "@permissionValidator.hasAssignRolePermission(#appCode)")
  @RequestMapping(value = "/apps/{appCode}/namespaces/{namespaceName}/roles/{roleType}", method = RequestMethod.POST)
  public ResponseEntity<Void> assignNamespaceRoleToUser(@PathVariable String appCode, @PathVariable String namespaceName,
                                                        @PathVariable String roleType, @RequestBody String user) {
    checkUserExists(user);
    RequestPrecondition.checkArgumentsNotEmpty(user);

    if (!RoleType.isValidRoleType(roleType)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "role type is illegal");
    }
    Set<String> assignedUser = rolePermissionService.assignRoleToUsers(RoleUtils.buildNamespaceRoleName(appCode, namespaceName, roleType),
        Sets.newHashSet(user), userInfoHolder.getUser().getUserId());
    if (CollectionUtils.isEmpty(assignedUser)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, user + "已授权");
    }

    return ResponseEntity.ok().build();
  }

  @PreAuthorize(value = "@permissionValidator.hasAssignRolePermission(#appCode)")
  @RequestMapping(value = "/apps/{appCode}/namespaces/{namespaceName}/roles/{roleType}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> removeNamespaceRoleFromUser(@PathVariable String appCode, @PathVariable String namespaceName,
                                                          @PathVariable String roleType, @RequestParam String user) {
    RequestPrecondition.checkArgumentsNotEmpty(user);

    if (!RoleType.isValidRoleType(roleType)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "role type is illegal");
    }
    rolePermissionService.removeRoleFromUsers(RoleUtils.buildNamespaceRoleName(appCode, namespaceName, roleType),
        Sets.newHashSet(user), userInfoHolder.getUser().getUserId());
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "/apps/{appCode}/role_users", method = RequestMethod.GET)
  public AppRolesAssignedUsers getAppRoles(@PathVariable String appCode) {
    AppRolesAssignedUsers users = new AppRolesAssignedUsers();
    users.setAppId(appCode);

    Set<UserInfo> masterUsers = rolePermissionService.queryUsersWithRole(RoleUtils.buildAppMasterRoleName(appCode));
    users.setMasterUsers(masterUsers);

    return users;
  }

  @PreAuthorize(value = "@permissionValidator.hasAssignRolePermission(#appCode)")
  @RequestMapping(value = "/apps/{appCode}/roles/{roleType}", method = RequestMethod.POST)
  public ResponseEntity<Void> assignAppRoleToUser(@PathVariable String appCode, @PathVariable String roleType,
                                                  @RequestBody String user) {
    checkUserExists(user);
    RequestPrecondition.checkArgumentsNotEmpty(user);

    if (!RoleType.isValidRoleType(roleType)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "role type is illegal");
    }
    Set<String> assignedUsers = rolePermissionService.assignRoleToUsers(RoleUtils.buildAppRoleName(appCode, roleType),
        Sets.newHashSet(user), userInfoHolder.getUser().getUserId());
    if (CollectionUtils.isEmpty(assignedUsers)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, user + "已授权");
    }

    return ResponseEntity.ok().build();
  }

  @PreAuthorize(value = "@permissionValidator.hasAssignRolePermission(#appCode)")
  @RequestMapping(value = "/apps/{appCode}/roles/{roleType}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> removeAppRoleFromUser(@PathVariable String appCode, @PathVariable String roleType,
                                                    @RequestParam String user) {
    RequestPrecondition.checkArgumentsNotEmpty(user);

    if (!RoleType.isValidRoleType(roleType)) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "role type is illegal");
    }
    rolePermissionService.removeRoleFromUsers(RoleUtils.buildAppRoleName(appCode, roleType),
        Sets.newHashSet(user), userInfoHolder.getUser().getUserId());
    return ResponseEntity.ok().build();
  }

  private void checkUserExists(String userId) {
    if (userService.findByUserId(userId) == null) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("User %s does not exist!", userId));
    }
  }

}
*/
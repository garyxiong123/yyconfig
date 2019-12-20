package com.yofish.apollo.spi.defaultimpl;

import com.google.common.collect.Sets;
import com.yofish.apollo.constant.PermissionType;
import com.yofish.apollo.constant.RoleType;
import com.yofish.apollo.domain.App;
import com.yofish.apollo.service.RoleInitializationService;
import com.yofish.apollo.util.RoleUtils;
import com.yofish.gary.biz.domain.Permission;
import com.yofish.gary.biz.domain.Role;
import com.yofish.gary.biz.repository.PermissionRepository;
import com.yofish.gary.biz.service.RoleService;
import com.yofish.gary.dao.entity.BaseEntity;
import framework.apollo.core.ConfigConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Service
public class DefaultRoleInitializationService implements RoleInitializationService {

    @Autowired
    private RoleService roleService;
//    @Autowired
//    private PortalConfig portalConfig;
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void initAppRoles(App app) {
        String appCode = app.getAppCode();

        String appMasterRoleName = RoleUtils.buildAppMasterRoleName(appCode);

        //has created before
        if (roleService.findByRoleName(appMasterRoleName) != null) {
            return;
        }
        /*String operator = app.getCreateAuthor();
        //create app permissions
        createAppMasterRole(appCode, operator);
        //create manageAppMaster permission
        createManageAppMasterRole(appCode, operator);

        //assign master role to user
        rolePermissionService
                .assignRoleToUsers(RoleUtils.buildAppMasterRoleName(appCode), Sets.newHashSet(app.getOwnerName()),
                        operator);

        initNamespaceRoles(appCode, ConfigConsts.NAMESPACE_APPLICATION, operator);
        initNamespaceEnvRoles(appCode, ConfigConsts.NAMESPACE_APPLICATION, operator);

        //assign modify、release appNamespace role to user
        rolePermissionService.assignRoleToUsers(
                RoleUtils.buildNamespaceRoleName(appCode, ConfigConsts.NAMESPACE_APPLICATION, RoleType.MODIFY_NAMESPACE),
                Sets.newHashSet(operator), operator);
        rolePermissionService.assignRoleToUsers(
                RoleUtils.buildNamespaceRoleName(appCode, ConfigConsts.NAMESPACE_APPLICATION, RoleType.RELEASE_NAMESPACE),
                Sets.newHashSet(operator), operator);*/

    }

/*

    private void createAppMasterRole(String appId, String operator) {
        Set<PermissionAuth> appPermissions =
                Stream.of(PermissionType.CREATE_CLUSTER, PermissionType.CREATE_NAMESPACE, PermissionType.ASSIGN_ROLE)
                        .map(permissionType -> createPermission(appId, permissionType, operator)).collect(Collectors.toSet());
        Set<PermissionAuth> createdAppPermissions = rolePermissionService.createPermissions(appPermissions);
        Set<Long>
                appPermissionIds =
                createdAppPermissions.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        //create app master role
        Role appMasterRole = createRole(RoleUtils.buildAppMasterRoleName(appId), operator);

        rolePermissionService.createRoleWithPermissions(appMasterRole, appPermissionIds);
    }

    private PermissionAuth createPermission(String targetId, String permissionType, String operator) {
        PermissionAuth permission = new PermissionAuth();
        permission.set(permissionType);
        permission.setTargetId(targetId);
        permission.setDataChangeCreatedBy(operator);
        permission.setDataChangeLastModifiedBy(operator);
        return permission;
    }
*/

    @Override
    public void initNamespaceRoles(String appId, String namespaceName, String operator) {

    }

    @Override
    public void initNamespaceEnvRoles(String appId, String namespaceName, String operator) {

    }

    @Override
    public void initNamespaceSpecificEnvRoles(String appId, String namespaceName, String env, String operator) {

    }

    @Override
    public void initCreateAppRole() {

    }

    @Override
    public void initManageAppMasterRole(String appId, String operator) {

    }
}

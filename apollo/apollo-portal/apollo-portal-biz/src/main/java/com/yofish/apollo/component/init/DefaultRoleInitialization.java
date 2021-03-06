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
package com.yofish.apollo.component.init;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.component.util.RoleUtils;
import com.yofish.gary.biz.repository.PermissionRepository;
import com.yofish.gary.biz.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Component
public class DefaultRoleInitialization {

    @Autowired
    private RoleService roleService;
    //    @Autowired
//    private PortalConfig portalConfig;
    @Autowired
    private PermissionRepository permissionRepository;

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

    private void createAppMasterRole(String appCode, String operator) {
        Set<PermissionAuth> appPermissions =
                Stream.of(PermissionType.CREATE_CLUSTER, PermissionType.CREATE_NAMESPACE, PermissionType.ASSIGN_ROLE)
                        .map(permissionType -> createPermission(appCode, permissionType, operator)).collect(Collectors.toSet());
        Set<PermissionAuth> createdAppPermissions = rolePermissionService.createPermissions(appPermissions);
        Set<Long>
                appPermissionIds =
                createdAppPermissions.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        //create app master role
        Role appMasterRole = createRole(RoleUtils.buildAppMasterRoleName(appCode), operator);

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

    public void initNamespaceRoles(String appId, String namespaceName, String operator) {

    }

    public void initNamespaceEnvRoles(String appId, String namespaceName, String operator) {

    }

    public void initNamespaceSpecificEnvRoles(String appId, String namespaceName, String env, String operator) {

    }

    public void initCreateAppRole() {

    }

    public void initManageAppMasterRole(String appId, String operator) {

    }
}

package com.yofish.apollo.component;

import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.apollo.service.PortalConfig;
import com.yofish.gary.biz.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.youyu.common.helper.YyRequestInfoHelper.getCurrentUserId;

@Component("permissionValidator")
public class PermissionValidator {

    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppRepository appRepository;

    public boolean hasModifyNamespacePermission(String appCode) {
        return isAppAdmin(appCode);
    }

    public boolean hasModifyNamespacePermission(String appCode, String namespaceName, String env) {
        return isAppAdmin(appCode);
    }

    public boolean hasReleaseNamespacePermission(String appCode, String namespaceName) {
        return isAppOwner(appCode);
    }

    public boolean hasReleaseNamespacePermission(String appCode) {
        return isAppOwner(appCode);
    }

    public boolean hasDeleteNamespacePermission(String appCode) {
        return isAppAdmin(appCode);
    }

    public boolean hasOperateNamespacePermission(String appCode, String namespaceName) {
        return isAppAdmin(appCode);
    }


    public boolean hasAssignRolePermission(String appCode) {
        return isAppAdmin(appCode);

    }

    public boolean hasCreateNamespacePermission(String appCode) {

        return isAppAdmin(appCode);

    }

    public boolean hasCreateAppNamespacePermission(String appCode, AppNamespace appNamespace) {

        boolean isPublicAppNamespace = appNamespace.isPublic();

        if (portalConfig.canAppAdminCreatePrivateNamespace() || isPublicAppNamespace) {
            return hasCreateNamespacePermission(appCode);
        }

        return isSuperAdmin();
    }

    public boolean hasCreateClusterPermission(String appCode) {
        return isAppAdmin(appCode);

    }

    public boolean isAppOwner(String appCode) {
        return isSuperAdmin() || appRepository.findByAppCode(appCode).getAppOwner().equals(getCurrentUserId());
    }


    public boolean isAppAdmin(String appCode) {
        return isSuperAdmin() || hasAssignRolePermission(appCode);
    }

    public boolean isSuperAdmin() {
        return userRepository.findById(getCurrentUserId()).get().isAdmin();
    }

    public boolean shouldHideConfigToCurrentUser() {
        return false;
    }
}

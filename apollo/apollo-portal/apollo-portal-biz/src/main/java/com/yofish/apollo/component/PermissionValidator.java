package com.yofish.apollo.component;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.AppNamespace4Public;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.apollo.service.PortalConfig;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.UserRepository;
import com.youyu.common.utils.YyAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        boolean isPublicAppNamespace = appNamespace instanceof AppNamespace4Public;

        if (portalConfig.canAppAdminCreatePrivateNamespace() || isPublicAppNamespace) {
            return hasCreateNamespacePermission(appCode);
        }

        return isSuperAdmin();
    }

    public boolean hasCreateClusterPermission(String appCode) {
        return isSuperAdmin() || isAppAdmin(getApp(appCode));
    }

    public boolean isSameDepartment(long appId) {
        App app = getApp(appId);
        return isSameDepartment(app);
    }

    public boolean isSameDepartment(String appCode) {
        App app = getApp(appCode);
        return isSameDepartment(app);
    }

    public boolean isSameDepartment(App app) {
        Department appDepartment = app.getDepartment();
        User user = userRepository.findById(getCurrentUserId()).get();
        boolean isSameDepartment = appDepartment.getId().equals(user.getDepartment().getId());
        return isSameDepartment;
    }

    public boolean isAppOwner(String appCode) {
        App app = getApp(appCode);
        return isAppOwner(app);
    }

    public boolean isAppOwner(long appId) {
        App app = getApp(appId);
        return isAppAdmin(app);
    }

    public boolean isAppOwner(App app) {
        return app.getAppOwner().getId().equals(getCurrentUserId());
    }


    public boolean isAppAdmin(String appCode) {
        App app = getApp(appCode);
        return isAppAdmin(app);
    }

    public boolean isAppAdmin(long appId) {
        App app = getApp(appId);
        return isAppAdmin(app);
    }

    public boolean isAppAdmin(App app) {
        Set<User> appAdmins = app.getAppAdmins();
        if (ObjectUtils.isEmpty(appAdmins)) {
            return false;
        } else {
            List<Long> adminIdList = app.getAppAdmins().stream().map(user -> user.getId()).collect(Collectors.toList());
            Long currentUserId = getCurrentUserId();
            return adminIdList.contains(currentUserId);
        }
    }

    private App getApp(long appId) {
        App app = appRepository.findById(appId).orElse(null);
        YyAssert.paramCheck(ObjectUtils.isEmpty(app), "appId:" + appId + " 不存在！");
        return app;
    }

    private App getApp(String appCode) {
        App app = appRepository.findByAppCode(appCode);
        YyAssert.paramCheck(ObjectUtils.isEmpty(app), "appCode:" + appCode + " 不存在！");
        return app;
    }

    /**
     * 当前用户是否是系统超级管理员
     *
     * @return
     */
    public boolean isSuperAdmin() {
        return userRepository.findById(getCurrentUserId()).get().isAdmin();
    }

    public boolean shouldHideConfigToCurrentUser() {
        return false;
    }
}

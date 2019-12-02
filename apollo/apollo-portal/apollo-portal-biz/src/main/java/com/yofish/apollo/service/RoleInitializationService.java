package com.yofish.apollo.service;

import com.yofish.apollo.domain.App;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
public interface RoleInitializationService {

    void initAppRoles(App app);

    void initNamespaceRoles(String appId, String namespaceName, String operator);

    void initNamespaceEnvRoles(String appId, String namespaceName, String operator);

    void initNamespaceSpecificEnvRoles(String appId, String namespaceName, String env, String operator);

    void initCreateAppRole();

    void initManageAppMasterRole(String appId, String operator);
}

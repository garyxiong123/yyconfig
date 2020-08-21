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
package com.yofish.apollo.service;


import com.yofish.apollo.component.PermissionValidator;
import com.yofish.apollo.domain.App;
import com.yofish.apollo.model.bo.EnvClusterInfo;
import com.yofish.apollo.model.AppModel;
import com.yofish.apollo.pattern.factory.AppFactory;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.gary.biz.repository.DepartmentRepository;
import com.yofish.gary.biz.service.UserService;
import com.youyu.common.api.PageData;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.yofish.yyconfig.common.common.utils.PageDataAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppService {

    @Autowired
    private AppRepository appRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private AppEnvClusterService clusterService;
    @Autowired
    private PermissionValidator permissionValidator;
    @Autowired
    private AppFactory appFactory;


    @Transactional(rollbackFor = Exception.class)
    public App createApp(@Valid AppModel appModel) {
        App app = appFactory.createApp(appModel);

        return app;
    }


    private List<App> filterWithAuthorize(List<App> all) {
        if (ObjectUtils.isEmpty(all)) {
            return Collections.emptyList();
        } else {
            return all.stream()
                    .filter(app -> permissionValidator.isAppOwner(app) || permissionValidator.isAppAdmin(app) || permissionValidator.isSameDepartment(app))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 根据当前登陆用户权限过滤
     *
     * @return
     */
    public List<App> findAllWithAuthorize() {
        List<App> all = this.appRepository.findAll();
        if (permissionValidator.isSuperAdmin()) {
            return all;
        } else {
            List<App> withAuthorize = filterWithAuthorize(all);
            return withAuthorize;
        }
    }

    /**
     * 根据当前登陆用户权限过滤（分页）
     *
     * @param pageable
     * @return
     */
    public PageData<App> findAllWithAuthorize(Pageable pageable) {
        if (permissionValidator.isSuperAdmin()) {
            //用户是管理员
            Page<App> apps = appRepository.findAll(pageable);
            return PageDataAdapter.toPageData(apps);
        } else {
            List<App> allWithAuthorize = findAllWithAuthorize();
            List<App> appsByPage = allWithAuthorize.subList(Long.valueOf(pageable.getOffset()).intValue(), allWithAuthorize.size() > pageable.getPageSize() ? pageable.getPageSize() : allWithAuthorize.size());
            return PageDataAdapter.toPageData(pageable, appsByPage, allWithAuthorize.size());
        }
    }

    /**
     * 根据当前登陆用户权限过滤（分页+条件查询）
     *
     * @param query
     * @param pageable
     * @return
     */
    public PageData<App> searchByAppCodeOrAppName(String query, Pageable pageable) {
        if (permissionValidator.isSuperAdmin()) {
            //用户是管理员
            Page<App> apps = appRepository.findByAppCodeContainingOrNameContaining(query, query, pageable);
            return PageDataAdapter.toPageData(apps);
        } else {
            List<App> allWithAuthorize = appRepository.findAllByAppCodeContainingOrNameContaining(query, query);
            List<App> appsByPage = allWithAuthorize.subList(Long.valueOf(pageable.getOffset()).intValue(), pageable.getPageSize());
            return PageDataAdapter.toPageData(pageable, appsByPage, allWithAuthorize.size());
        }
    }

    /**
     * 只能改 appAdmins
     *
     * @param app
     * @return
     */
    @Transactional
    public App updateApp(App app) {
        Long appId = app.getId();

        App managedApp = appRepository.findById(appId).orElse(null);
        if (managedApp == null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App not exists. AppId = %s", appId));
        }

        managedApp.setName(app.getName());
        managedApp.setDepartment(app.getDepartment());
        app.checkOwnerAndAdminsAndDepartmentIsExist();

        managedApp.setAppAdmins(app.getAppAdmins());

        return appRepository.save(app);
    }

    public App getAppById(long appId) {
        App app = appRepository.findById(appId).orElse(null);
        return app;
    }

    public App getAppByCode(String appCode) {
        App app = appRepository.findByAppCode(appCode);
        return app;
    }

    public EnvClusterInfo createEnvNavNode(String env, long appId) {
        EnvClusterInfo node = new EnvClusterInfo(env);
        node.setClusters(clusterService.findClusters(env, appId));
        return node;
    }

    public EnvClusterInfo createEnvNavNode(String env, String appCode) {
        EnvClusterInfo node = new EnvClusterInfo(env);
        node.setClusters(clusterService.findClusters(env, appCode));
        return node;
    }
}

package com.yofish.apollo.service;


import com.yofish.apollo.component.PermissionValidator;
import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.model.vo.EnvClusterInfo;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.DepartmentRepository;
import com.yofish.gary.biz.repository.UserRepository;
import com.yofish.gary.biz.service.UserService;
import com.youyu.common.api.PageData;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import common.utils.PageDataAdapter;
import framework.apollo.core.ConfigConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppService {

    @Autowired
    private AppRepository appRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private AppNamespaceService appNamespaceService;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;
    @Autowired
    private AppEnvClusterService appEnvClusterService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private AppEnvClusterService clusterService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionValidator permissionValidator;


    @Transactional(rollbackFor = Exception.class)
    public App createApp(App app) {
        String appCode = app.getAppCode();

        App managedApp = appRepository.findByAppCode(appCode);

        if (managedApp != null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App already exists. AppCode = %s", appCode));
        }
        this.checkOwnerAndAdminsAndDepartmentIsExist(app);

        App createdApp = appRepository.save(app);

        AppNamespace defaultAppNamespace = appNamespaceService.createDefaultAppNamespace(createdApp.getId());

        appEnvClusterService.createClusterInEachActiveEnv(createdApp.getId(), ConfigConsts.CLUSTER_NAME_DEFAULT);

        appEnvClusterNamespaceService.createNamespaceForAppNamespaceInAllCluster(defaultAppNamespace);

        return createdApp;
    }

    private void checkOwnerAndAdminsAndDepartmentIsExist(App app) {
        //owner
        com.yofish.gary.api.dto.rsp.UserDetailRspDTO userDetail = userService.getUserDetail(app.getAppOwner().getId());
        if (userDetail == null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Application's owner not exist.");
        }

        //admins
        this.checkAppAdminsIsExist(app.getAppAdmins());

        //department
        Department department = departmentRepository.findById(app.getDepartment().getId()).orElse(null);
        if (department == null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Application's department not exist.");
        }
    }

    private void checkAppAdminsIsExist(Set<User> appAdmins) {
        if (!ObjectUtils.isEmpty(appAdmins)) {
            for (User appAdmin : appAdmins) {
                com.yofish.gary.api.dto.rsp.UserDetailRspDTO userDetailRspDTO = userService.getUserDetail(appAdmin.getId());
                if (userDetailRspDTO == null) {
                    throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Application's admin [" + appAdmin.getId() + "] not exist.");
                }
            }
        }
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
            List<App> appsByPage = allWithAuthorize.subList(Long.valueOf(pageable.getOffset()).intValue(), allWithAuthorize.size()>pageable.getPageSize()?pageable.getPageSize():allWithAuthorize.size());
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

        this.checkOwnerAndAdminsAndDepartmentIsExist(app);

        managedApp.setAppAdmins(app.getAppAdmins());

        return appRepository.save(app);
    }

    public App getApp(long appId) {
        App app = appRepository.findById(appId).orElse(null);
        return app;
    }

    public App getApp(String appCode) {
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
/*
  public List<App> findAll() {
    Iterable<App> apps = appRepository.findAll();
    if (apps == null) {
      return Collections.emptyList();
    }
    return Lists.newArrayList((apps));
  }

  public PageDTO<App> findAll(Pageable pageable) {
    Page<App> apps = appRepository.findAll(pageable);

    return new PageDTO<>(apps.getContent(), pageable, apps.getTotalElements());
  }
  public PageDTO<App> searchByAppCodeOrAppName(String query, Pageable pageable) {
    Page<App> apps = appRepository.findByAppIdContainingOrNameContaining(query, query, pageable);

    return new PageDTO<>(apps.getContent(), pageable, apps.getTotalElements());
  }

  public List<App> findByAppIds(Set<String> appIds) {
    return appRepository.findByAppIdIn(appIds);
  }

  public List<App> findByAppIds(Set<String> appIds, Pageable pageable) {
    return appRepository.findByAppIdIn(appIds, pageable);
  }

  public List<App> findByOwnerName(String ownerName, Pageable page) {
    return appRepository.findByOwnerName(ownerName, page);
  }

  public App load(String appCode) {
    return appRepository.findByAppId(appCode);
  }

  public AppDTO load(Env env, String appCode) {
    return appAPI.loadApp(env, appCode);
  }

  public void createAppInRemote(Env env, App app) {
    String username = userInfoHolder.getUser().getUserId();
    app.setDataChangeCreatedBy(username);
    app.setDataChangeLastModifiedBy(username);

    AppDTO appDTO = BeanUtils.transform(AppDTO.class, app);
    appAPI.createApp(env, appDTO);
  }

  @Transactional
  public App updateAppInLocal(App app) {
    String appCode = app.getAppId();

    App managedApp = appRepository.findByAppId(appCode);
    if (managedApp == null) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App not exists. AppId = %s", appCode));
    }

    managedApp.setName(app.getName());
    managedApp.setOrgId(app.getOrgId());
    managedApp.setOrgName(app.getOrgName());

    String ownerName = app.getOwnerName();
    UserInfo owner = userService.findByUserId(ownerName);
    if (owner == null) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App's owner not exists. owner = %s", ownerName));
    }
    managedApp.setOwnerName(owner.getUserId());
    managedApp.setOwnerEmail(owner.getEmail());

    String operator = userInfoHolder.getUser().getUserId();
    managedApp.setDataChangeLastModifiedBy(operator);

    return appRepository.save(managedApp);
  }

  @Transactional
  public App deleteAppInLocal(String appCode) {
    App managedApp = appRepository.findByAppId(appCode);
    if (managedApp == null) {
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App not exists. AppId = %s", appCode));
    }
    String operator = userInfoHolder.getUser().getUserId();

    //this operator is passed to com.ctrip.framework.apollo.portal.listener.DeletionListener.onAppDeletionEvent
    managedApp.setDataChangeLastModifiedBy(operator);

    //删除portal数据库中的app
    appRepository.deleteApp(appCode, operator);

    //删除portal数据库中的appNamespace
    appNamespaceService.batchDeleteByAppId(appCode, operator);

    //删除portal数据库中的收藏表
    favoriteService.batchDeleteByAppId(appCode, operator);

    //删除portal数据库中Permission、Role相关数据
    rolePermissionService.deleteRolePermissionsByAppId(appCode, operator);

    return managedApp;
  }*/
}

package com.yofish.apollo.service;


import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Department;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.apollo.repository.DepartmentRepository;
import com.yofish.gary.api.dto.rsp.UserDetailRspDTO;
import com.yofish.gary.biz.service.UserService;
import com.youyu.common.api.PageData;
import com.youyu.common.helper.YyRequestInfoHelper;
import common.exception.BadRequestException;
import common.utils.PageDataAdapter;
import framework.apollo.core.ConfigConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public App createApp(App app) {
        String appCode = app.getAppCode();

        App managedApp = appRepository.findByAppCode(appCode);

        if (managedApp != null) {
            throw new BadRequestException(String.format("App already exists. AppCode = %s", appCode));
        }

        com.yofish.gary.api.dto.rsp.UserDetailRspDTO userDetail = userService.getUserDetail(app.getAppOwner().getId());
        if (userDetail == null) {
            throw new BadRequestException("Application's owner not exist.");
        }

        Department department = departmentRepository.findById(app.getDepartment().getId()).orElse(null);
        if (department == null) {
            throw new BadRequestException("Application's department not exist.");
        }

        App createdApp = appRepository.save(app);

        appNamespaceService.createDefaultAppNamespace(createdApp.getId());

        appEnvClusterService.createDefaultCluster(createdApp.getId());

        appEnvClusterNamespaceService.createNamespaceForAppNamespaceInAllCluster(createdApp.getId(), ConfigConsts.NAMESPACE_APPLICATION);

        return createdApp;
    }


    public PageData<App> findAll(Pageable pageable) {
        Page<App> apps = appRepository.findAll(pageable);

        return PageDataAdapter.toPageData(apps);
    }

    public PageData<App> searchByAppCodeOrAppName(String query, Pageable pageable) {
        Page<App> apps = appRepository.findByAppCodeContainingOrNameContaining(query, query, pageable);

        return PageDataAdapter.toPageData(apps);
    }


    @Transactional
    public App updateApp(App app) {
        Long appId = app.getId();

        App managedApp = appRepository.findById(appId).orElse(null);
        if (managedApp == null) {
            throw new BadRequestException(String.format("App not exists. AppId = %s", appId));
        }

        managedApp.setName(app.getName());
        managedApp.setDepartment(app.getDepartment());

        Long userId = app.getAppOwner().getId();
        UserDetailRspDTO userDetail = userService.getUserDetail(userId);
        if (userDetail == null) {
            throw new BadRequestException(String.format("App's owner not exists. owner = %s", userId));
        }

        Department department = departmentRepository.findById(app.getDepartment().getId()).orElse(null);
        if (department == null) {
            throw new BadRequestException("Application's department not exist.");
        }

        managedApp.setAppOwner(app.getAppOwner());

        String operator = YyRequestInfoHelper.getCurrentUserName();
        managedApp.setUpdateAuthor(operator);

        return appRepository.save(managedApp);
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

  public App load(String appId) {
    return appRepository.findByAppId(appId);
  }

  public AppDTO load(Env env, String appId) {
    return appAPI.loadApp(env, appId);
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
    String appId = app.getAppId();

    App managedApp = appRepository.findByAppId(appId);
    if (managedApp == null) {
      throw new BadRequestException(String.format("App not exists. AppId = %s", appId));
    }

    managedApp.setName(app.getName());
    managedApp.setOrgId(app.getOrgId());
    managedApp.setOrgName(app.getOrgName());

    String ownerName = app.getOwnerName();
    UserInfo owner = userService.findByUserId(ownerName);
    if (owner == null) {
      throw new BadRequestException(String.format("App's owner not exists. owner = %s", ownerName));
    }
    managedApp.setOwnerName(owner.getUserId());
    managedApp.setOwnerEmail(owner.getEmail());

    String operator = userInfoHolder.getUser().getUserId();
    managedApp.setDataChangeLastModifiedBy(operator);

    return appRepository.save(managedApp);
  }

  public EnvClusterInfo createEnvNavNode(Env env, String appId) {
    EnvClusterInfo node = new EnvClusterInfo(env);
    node.setClusters(clusterService.findClusters(env, appId));
    return node;
  }

  @Transactional
  public App deleteAppInLocal(String appId) {
    App managedApp = appRepository.findByAppId(appId);
    if (managedApp == null) {
      throw new BadRequestException(String.format("App not exists. AppId = %s", appId));
    }
    String operator = userInfoHolder.getUser().getUserId();

    //this operator is passed to com.ctrip.framework.apollo.portal.listener.DeletionListener.onAppDeletionEvent
    managedApp.setDataChangeLastModifiedBy(operator);

    //删除portal数据库中的app
    appRepository.deleteApp(appId, operator);

    //删除portal数据库中的appNamespace
    appNamespaceService.batchDeleteByAppId(appId, operator);

    //删除portal数据库中的收藏表
    favoriteService.batchDeleteByAppId(appId, operator);

    //删除portal数据库中Permission、Role相关数据
    rolePermissionService.deleteRolePermissionsByAppId(appId, operator);

    return managedApp;
  }*/
}

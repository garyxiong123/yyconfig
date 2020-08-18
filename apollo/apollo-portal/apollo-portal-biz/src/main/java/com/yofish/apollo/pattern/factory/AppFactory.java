package com.yofish.apollo.pattern.factory;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Main;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.model.AppModel;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.apollo.pattern.util.ServerConfigUtil;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import framework.apollo.core.ConfigConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/7/26 下午10:31
 */
@Component
public class AppFactory {
    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    private ServerConfigUtil serverConfigUtil;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;

    /**
     * 创建App
     * @param createAppModel
     * @return
     */
    public App createApp(AppModel createAppModel) {
        paramCheck(createAppModel);

        App createdApp = new App(createAppModel);

        doCreateApp(createdApp);

        AppNamespace defaultAppNamespace = createDefaultAppNamespace(createdApp.getId());

        createAppEnvCluster4AllActiveEnv(createdApp.getId(), ConfigConsts.CLUSTER_NAME_DEFAULT);

        createNamespace4AllCluster(defaultAppNamespace);
        return createdApp;
    }

    private void doCreateApp(App app) {
        appRepository.save(app);
    }

    private void paramCheck(AppModel appModel) {

        App managedApp = appRepository.findByAppCode(appModel.getAppCode());
        if (managedApp != null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App already exists. AppCode = %s", appModel.getAppCode()));
        }
    }


    @Transactional
    public AppNamespace createDefaultAppNamespace(Long appId) {
        AppNamespace appNs = new AppNamespace();
        appNs.buildDefaultAppNamespace(appId);

        appNs = appNamespaceRepository.save(appNs);
        return appNs;
    }

    public void createNamespace4AllCluster(AppNamespace appNamespace) {
        List<AppEnvCluster> appEnvClusters = this.appEnvClusterRepository.findByApp(appNamespace.getApp());
        for (AppEnvCluster appEnvCluster : appEnvClusters) {

            AppEnvClusterNamespace4Main appEnvClusterNamespace = new AppEnvClusterNamespace4Main(appEnvCluster, appNamespace);
            appEnvClusterNamespaceRepository.save(appEnvClusterNamespace);
        }
    }


    @Transactional
    public void createAppEnvCluster4AllActiveEnv(long appId, String clusterName) {
        List<String> envs = serverConfigUtil.getActiveEnvs();
        //每次遍历，都要new，防止覆盖
        envs.forEach((env -> {
                    AppEnvCluster appEnvCluster = new AppEnvCluster(null, env, clusterName, new App(appId));
                    appEnvClusterRepository.save(appEnvCluster);
                })
        );

    }
}
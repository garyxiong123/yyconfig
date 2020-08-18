package com.yofish.apollo.pattern.factory;

import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Main;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.model.AppNamespaceModel;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.yofish.apollo.service.AppEnvClusterNamespaceService;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/7/26 上午11:26
 */
@Component
public class AppNamespaceFactory {
    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;

    public AppNamespace createAppNamespace(AppNamespaceModel appNamespaceModel) {

        AppNamespace appNamespace = new AppNamespace(appNamespaceModel);


        if (!appNamespace.isAppNamespaceNameUnique()) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App already has application appNamespace. AppId = %s", appNamespace.getApp().getId()));
        }
        if (appNamespace.isPublicOrProtect()) {
            appNamespace.checkAppNamespaceGlobalUniqueness();
        }

        appNamespaceRepository.save(appNamespace);

        createNamespaceForAllCluster(appNamespace);
        return appNamespace;
    }


    /**
     * 为所有集群 创建 命名空间
     *
     * @param appNamespace
     */
    public void createNamespaceForAllCluster(AppNamespace appNamespace) {
        List<AppEnvCluster> appEnvClusters = this.appEnvClusterRepository.findByApp(appNamespace.getApp());
        for (AppEnvCluster appEnvCluster : appEnvClusters) {
            AppEnvClusterNamespace4Main appEnvClusterNamespace = new AppEnvClusterNamespace4Main(appEnvCluster, appNamespace);
            appEnvClusterNamespaceRepository.save(appEnvClusterNamespace);
        }
    }

}

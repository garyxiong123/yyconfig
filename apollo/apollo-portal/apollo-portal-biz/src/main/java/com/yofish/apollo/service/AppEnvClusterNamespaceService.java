package com.yofish.apollo.service;

import com.yofish.apollo.domain.*;
import com.yofish.apollo.repository.AppEnvClusterNamespace4BranchRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Service
public class AppEnvClusterNamespaceService {
    @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;
    @Autowired
    private AppNamespaceService appNamespaceService;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private ServerConfigService serverConfigService;
    @Autowired
    private AppEnvClusterNamespace4BranchRepository branchRepository;

    public AppEnvClusterNamespace4Branch findChildNamespace(Long parentId) {
        return branchRepository.findByParentId(parentId);
    }

/*
    public NamespaceDTO createNamespace(String env, NamespaceDTO dto) {
        Namespace entity = BeanUtils.transform(Namespace.class, dto);
        Namespace managedEntity = this.appEnvClusterNamespaceRepository.findOne(Example.of(new Namespace(dto.getAppId(), env, dto.getClusterName(), dto.getNamespaceName()))).orElse(null);
        if (managedEntity != null) {
            throw new BadRequestException("appNamespace already exist.");
        }

        entity = this.appEnvClusterNamespaceRepository.save(entity);

        return BeanUtils.transform(NamespaceDTO.class, entity);
    }*/

    public boolean isNamespaceUnique(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        Objects.requireNonNull(appEnvCluster, "appEnvCluster must not be null");
        Objects.requireNonNull(appNamespace, "appNamespace must not be null");
        return Objects.isNull(appEnvClusterNamespaceRepository.findByAppEnvClusterAndAppNamespace(appEnvCluster, appNamespace));
    }

    @Transactional
    public void instanceOfAppNamespaces(AppEnvCluster appEnvCluster) {
        List<AppNamespace> appNamespaces = appNamespaceService.findByAppId(appEnvCluster.getApp().getId());

        for (AppNamespace appNamespace : appNamespaces) {
            AppEnvClusterNamespace4Main ns = new AppEnvClusterNamespace4Main(appEnvCluster, appNamespace);
            appEnvClusterNamespaceRepository.save(ns);
        }

    }

    public void createNamespaceForAppNamespaceInAllCluster(AppNamespace appNamespace) {
        List<AppEnvCluster> appEnvClusters = this.appEnvClusterRepository.findByApp(appNamespace.getApp());
        for (AppEnvCluster appEnvCluster : appEnvClusters) {
            // in case there is some dirty data, e.g. public appNamespace deleted in other app and now created in this app
            if (!this.isNamespaceUnique(appEnvCluster, appNamespace)) {
                continue;
            }

            AppEnvClusterNamespace appEnvClusterNamespace = new AppEnvClusterNamespace(appEnvCluster, appNamespace);
            appEnvClusterNamespaceRepository.save(appEnvClusterNamespace);
        }
    }

    public AppEnvClusterNamespace findAppEnvClusterNamespace(String appCode, String env, String namespace, String cluster, String type) {
        return appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(appCode, env, namespace, cluster, type);
    }

    public AppEnvClusterNamespace findAppEnvClusterNamespace(Long id) {
        return appEnvClusterNamespaceRepository.findAppEnvClusterNamespaceById(id);
    }
}

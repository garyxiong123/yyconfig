package com.yofish.apollo.service;

import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Branch;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.repository.AppEnvClusterNamespace4BranchRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Service
public class AppEnvClusterNamespaceService {
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    @Autowired
    private AppNamespaceService appNamespaceService;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private ServerConfigService serverConfigService;
    @Autowired
    private AppEnvClusterNamespace4BranchRepository branchRepository;

    public  AppEnvClusterNamespace4Branch findChildNamespace(Long parentId) {
        return branchRepository.findByParentId(parentId);
    }

/*
    public NamespaceDTO createNamespace(String env, NamespaceDTO dto) {
        Namespace entity = BeanUtils.transform(Namespace.class, dto);
        Namespace managedEntity = this.namespaceRepository.findOne(Example.of(new Namespace(dto.getAppId(), env, dto.getClusterName(), dto.getNamespaceName()))).orElse(null);
        if (managedEntity != null) {
            throw new BadRequestException("appNamespace already exist.");
        }

        entity = this.namespaceRepository.save(entity);

        return BeanUtils.transform(NamespaceDTO.class, entity);
    }*/

    public boolean isNamespaceUnique(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        Objects.requireNonNull(appEnvCluster, "appEnvCluster must not be null");
        Objects.requireNonNull(appNamespace, "appNamespace must not be null");
        return Objects.isNull(namespaceRepository.findByAppEnvClusterAndAppNamespace(appEnvCluster, appNamespace));
    }

/*    @Transactional
    public void instanceOfAppNamespaces(Long appId, String clusterName) {

        List<AppNamespace> appNamespaces = appNamespaceService.findByAppId(appId);

        for (AppNamespace appNamespace : appNamespaces) {
            Namespace ns = new Namespace();
            ns.setAppId(appId);
            ns.setClusterName(clusterName);
            ns.setNamespaceName(appNamespace.getName());
            namespaceRepository.save(ns);
        }

    }*/

    public void createNamespaceForAppNamespaceInAllCluster(AppNamespace appNamespace) {
        List<AppEnvCluster> appEnvClusters = this.appEnvClusterRepository.findByApp(appNamespace.getApp());
        for (AppEnvCluster appEnvCluster : appEnvClusters) {
            // in case there is some dirty data, e.g. public appNamespace deleted in other app and now created in this app
            if (!this.isNamespaceUnique(appEnvCluster, appNamespace)) {
                continue;
            }

            AppEnvClusterNamespace appEnvClusterNamespace = new AppEnvClusterNamespace(appEnvCluster, appNamespace);
            namespaceRepository.save(appEnvClusterNamespace);
        }
    }

    public AppEnvClusterNamespace findAppEnvClusterNamespace(String appCode,String env,String namespace,String cluster,String type){
        return namespaceRepository.findAppEnvClusterNamespace(appCode,env,namespace,cluster,type);
    }
    public AppEnvClusterNamespace findAppEnvClusterNamespace(Long id){
        return namespaceRepository.findAppEnvClusterNamespaceById(id);
    }
}

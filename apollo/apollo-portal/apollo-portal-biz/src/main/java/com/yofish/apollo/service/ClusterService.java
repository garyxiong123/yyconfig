package com.yofish.apollo.service;
//
//import com.google.common.base.Strings;
//import com.yofish.apollo.repository.ClusterRepository;
//import common.exception.BadRequestException;
//import common.utils.BeanUtils;
//import framework.apollo.core.ConfigConsts;
//import framework.apollo.core.enums.Env;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.repository.ClusterRepository;
import common.exception.ServiceException;
import framework.apollo.core.ConfigConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.ObjectUtils;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//
////import com.ctrip.framework.apollo.portal.api.AdminServiceAPI;
//
@Service
public class ClusterService {


    @Autowired
    private ClusterRepository clusterRepository;
    @Autowired
    private ServerConfigService serverConfigService;
//    @Autowired
//    private NamespaceService namespaceService;
//
//    @Autowired
//    private PortalSettings portalSettings;
//
//
//    public List<ClusterEntity> findClusters(Env env, String appId) {
//
//        return clusterRepository.findByAppIdAndEnv(appId, env.name());
//    }
//

    public AppEnvCluster createCluster(String env, AppEnvCluster appEnvCluster) {
        appEnvCluster.setEnv(env);
        return clusterRepository.save(appEnvCluster);
    }

    //    public void deleteCluster(Env env, String appId, String clusterName) {
////    clusterAPI.delete(env, appId, clusterName, userInfoHolder.getUser().getUserId());
//        ClusterEntity clusterEntity = new ClusterEntity();
//        clusterEntity.setEnv(env.name());
//        clusterEntity.setAppId(appId);
//        clusterRepository.delete(clusterEntity);
//        return;
//    }
//
//    public ClusterEntity loadCluster(String appId, Env env, String clusterName) {
////    return clusterAPI.loadCluster(appId, env, clusterName);
//        return clusterRepository.findByAppIdAndNameAndEnv(appId, clusterName, env.name());
//    }
//
//
    public boolean isClusterNameUnique(Long appId, String clusterName) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(clusterName, "ClusterName must not be null");
        return ObjectUtils.isEmpty((clusterRepository.findByAppAndName(new App(appId), clusterName)));

    }
//
//    public ClusterEntity findOne(String appId, String name, String env) {
//        //TODO fix env
//        return clusterRepository.findByAppIdAndNameAndEnv(appId, name, env);
//    }
//
//    public ClusterEntity findOne(long clusterId) {
//        return clusterRepository.findById(clusterId).get();
//    }
//
//    public List<ClusterEntity> findParentClusters(String appId) {
//        if (Strings.isNullOrEmpty(appId)) {
//            return Collections.emptyList();
//        }
//
//        List<ClusterEntity> clusterEntities = clusterRepository.findByAppIdAndParentClusterId(appId, 0L);
//        if (clusterEntities == null) {
//            return Collections.emptyList();
//        }
//
//        Collections.sort(clusterEntities);
//
//        return clusterEntities;
//    }
//
//    @Transactional
//    public ClusterEntity saveWithInstanceOfAppNamespaces(ClusterEntity entity) {
//
//        ClusterEntity savedClusterEntity = saveWithoutInstanceOfAppNamespaces(entity);
//
//        namespaceService.instanceOfAppNamespaces(savedClusterEntity.getAppId(), savedClusterEntity.getName(),
//                savedClusterEntity.getDataChangeCreatedBy());
//
//        return savedClusterEntity;
//    }
//
//
//    @Transactional
//    public ClusterEntity saveWithoutInstanceOfAppNamespaces(ClusterEntity entity) {
//        if (!isClusterNameUnique(entity.getAppId(), entity.getName())) {
//            throw new BadRequestException("clusterEntity not unique");
//        }
//        entity.setId(0);//protection
//        ClusterEntity clusterEntity = clusterRepository.save(entity);
//
//        auditService.audit(ClusterEntity.class.getSimpleName(), clusterEntity.getId(), AuditEntity.OP.INSERT,
//                clusterEntity.getDataChangeCreatedBy());
//
//        return clusterEntity;
//    }
//
//    @Transactional
//    public void delete(long id, String operator) {
//        ClusterEntity clusterEntity = clusterRepository.findById(id).get();
//        if (clusterEntity == null) {
//            throw new BadRequestException("clusterEntity not exist");
//        }
//
//        //delete linked namespaces
//        namespaceService.deleteByAppIdAndClusterName(clusterEntity.getAppId(), clusterEntity.getName(), operator);
//
//        clusterEntity.setDeleted(true);
//        clusterEntity.setDataChangeLastModifiedBy(operator);
//        clusterRepository.save(clusterEntity);
//
//        auditService.audit(ClusterEntity.class.getSimpleName(), id, AuditEntity.OP.DELETE, operator);
//    }
//
//    @Transactional
//    public ClusterEntity update(ClusterEntity clusterEntity) {
//        ClusterEntity managedClusterEntity =
//                clusterRepository.findByAppIdAndNameAndEnv(clusterEntity.getAppId(), clusterEntity.getName(), clusterEntity.getEnv());
//        BeanUtils.copyEntityProperties(clusterEntity, managedClusterEntity);
//        managedClusterEntity = clusterRepository.save(managedClusterEntity);
//
//        auditService.audit(ClusterEntity.class.getSimpleName(), managedClusterEntity.getId(), AuditEntity.OP.UPDATE,
//                managedClusterEntity.getDataChangeLastModifiedBy());
//
//        return managedClusterEntity;
//    }

    @Transactional
    public void createDefaultCluster(long appId) {
        if (!isClusterNameUnique(appId, ConfigConsts.CLUSTER_NAME_DEFAULT)) {
            throw new ServiceException("clusterEntity not unique");
        }
        List<String> envs = serverConfigService.getActiveEnvs();
        //每次遍历，都要new，防止覆盖
        envs.forEach((env -> {
                    AppEnvCluster appEnvCluster = new AppEnvCluster();
                    appEnvCluster.setName(ConfigConsts.CLUSTER_NAME_DEFAULT);
                    appEnvCluster.setApp(new App(appId));
                    appEnvCluster.setEnv(env);
                    clusterRepository.save(appEnvCluster);
                })

        );

    }

//    public List<ClusterEntity> findChildClusters(String appId, String parentClusterName, String env) {
//        //TODO fix
//        ClusterEntity parentClusterEntity = findOne(appId, parentClusterName, env);
//        if (parentClusterEntity == null) {
//            throw new BadRequestException("parent appEnvCluster not exist");
//        }
//
//        return clusterRepository.findByParentClusterId(parentClusterEntity.getId());
//    }
//
//    public List<ClusterEntity> findClusters(String appId) {
//        List<ClusterEntity> clusterEntities = clusterRepository.findByAppId(appId);
//
//        if (clusterEntities == null) {
//            return Collections.emptyList();
//        }
//
//        // to make sure parent appEnvCluster is ahead of branch appEnvCluster
//        Collections.sort(clusterEntities);
//
//        return clusterEntities;
//    }
//
//
}

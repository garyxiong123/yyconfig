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

import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.model.dto.Req.CreateClusterReqDTO;
import com.yofish.apollo.pattern.factory.AppEnvClusterFactory;
import com.yofish.apollo.pattern.util.ServerConfigUtil;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//
@Service
public class AppEnvClusterService {
    @Autowired
    private AppEnvClusterFactory appEnvClusterFactory;

    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private ServerConfigUtil serverConfigUtil;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;


    public List<AppEnvCluster> findClusters(String env, long appId) {
        return appEnvClusterRepository.findByAppIdAndEnv(appId, env);
    }

    public List<AppEnvCluster> findClusters(String env, String appCode) {
        return appEnvClusterRepository.findByAppAppCodeAndEnv(appCode, env);
    }


    public AppEnvCluster getAppEnvCluster(long appId, String env, String clusterName) {
        return appEnvClusterRepository.findClusterByAppIdAndEnvAndName(appId, env, clusterName);
    }

    public void deleteAppEnvCluster(AppEnvCluster appEnvCluster) {
        appEnvClusterRepository.delete(appEnvCluster);
        // TODO: 2019-12-20 delete linked namespaces
    }

    public List<AppEnvCluster> createAppEnvCluster(CreateClusterReqDTO createClusterReqDTO) {
        return appEnvClusterFactory.createAppEnvClusters(createClusterReqDTO);
    }


    //    public void deleteCluster(Env env, String appCode, String clusterName) {
////    clusterAPI.delete(env, appCode, clusterName, userInfoHolder.getUser().getUserId());
//        ClusterEntity clusterEntity = new ClusterEntity();
//        clusterEntity.setEnv(env.name());
//        clusterEntity.setAppId(appCode);
//        clusterRepository.delete(clusterEntity);
//        return;
//    }
//
//    public ClusterEntity loadCluster(String appCode, Env env, String clusterName) {
////    return clusterAPI.loadCluster(appCode, env, clusterName);
//        return clusterRepository.findByAppIdAndNameAndEnv(appCode, clusterName, env.name());
//    }
//
//
//
//    public ClusterEntity findOne(String appCode, String name, String env) {
//        //TODO fix env
//        return clusterRepository.findByAppIdAndNameAndEnv(appCode, name, env);
//    }
//
//    public ClusterEntity findOne(long clusterId) {
//        return clusterRepository.findById(clusterId).get();
//    }
//
//    public List<ClusterEntity> findParentClusters(String appCode) {
//        if (Strings.isNullOrEmpty(appCode)) {
//            return Collections.emptyList();
//        }
//
//        List<ClusterEntity> clusterEntities = clusterRepository.findByAppIdAndParentClusterId(appCode, 0L);
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
//            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "clusterEntity not unique");
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
//            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "clusterEntity not exist");
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


}

//    public List<ClusterEntity> findChildClusters(String appCode, String parentClusterName, String env) {
//        //TODO fix
//        ClusterEntity parentClusterEntity = findOne(appCode, parentClusterName, env);
//        if (parentClusterEntity == null) {
//            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "parent appEnvCluster not exist");
//        }
//
//        return clusterRepository.findByParentClusterId(parentClusterEntity.getId());
//    }
//
//    public List<ClusterEntity> findClusters(String appCode) {
//        List<ClusterEntity> clusterEntities = clusterRepository.findByAppId(appCode);
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

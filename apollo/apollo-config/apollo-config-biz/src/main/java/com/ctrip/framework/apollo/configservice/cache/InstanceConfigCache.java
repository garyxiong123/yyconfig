package com.ctrip.framework.apollo.configservice.cache;

import com.ctrip.framework.apollo.configservice.domain.InstanceConfigRefresh;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Instance;
import com.yofish.apollo.domain.InstanceConfig;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.yofish.apollo.repository.InstanceConfigRepository;
import com.yofish.apollo.repository.InstanceRepository;
import com.yofish.apollo.service.InstanceService;
import com.yofish.yyconfig.common.common.NamespaceBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 心跳执行器
 * @Date: 2020/4/14 上午11:46
 */
@Component
public class InstanceConfigCache {
    /**
     * 10 minutes
     **/
    private static final long OFFER_TIME_LAST_MODIFIED_TIME_THRESHOLD_IN_MILLI = TimeUnit.MINUTES.toMillis(10);
    private Cache<String, Long> instanceIdCache;
    private Cache<String, String> releaseKeyCache;
    private static final int INSTANCE_CACHE_MAX_SIZE = 50000;
    private static final int INSTANCE_CONFIG_CACHE_MAX_SIZE = 50000;

    @Autowired
    private InstanceService instanceService;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private InstanceConfigRepository instanceConfigRepository;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;

    public InstanceConfigCache() {
        instanceIdCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).maximumSize(INSTANCE_CACHE_MAX_SIZE).build();
        releaseKeyCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).maximumSize(INSTANCE_CONFIG_CACHE_MAX_SIZE).build();
    }


    /**
     * 执行心跳
     * 1： 刷新实例
     * 2： 刷新实例配置
     *
     * @param instanceConfigRefresh
     */
    public void doInstanceRefresh(InstanceConfigRefresh instanceConfigRefresh) {

        Long instanceId = refreshInstance(instanceConfigRefresh);

        refreshInstanceConfig(instanceConfigRefresh, instanceId);
    }


    /**
     * 刷新实例: 1从内存里面拿 2：没有就走DB 3： 再没就自建， 同时放入缓存
     *
     * @param instanceConfigModel
     * @return
     */
    private Long refreshInstance(InstanceConfigRefresh instanceConfigModel) {

        String instanceKey = instanceConfigModel.assembleInstanceKey();

        Long instanceId = loadFromMemory(instanceKey);
        if (instanceId != null) return instanceId;

        instanceId = loadFromDb(instanceConfigModel);
        if (instanceId != null) return instanceId;

        Instance instance = createInstanceByModel(instanceConfigModel);

        instanceIdCache.put(instanceKey, instance.getId());

        return instance.getId();
    }

    /**
     * 刷新实例配置
     *
     * @param auditModel
     * @param instanceId
     */
    private void refreshInstanceConfig(InstanceConfigRefresh auditModel, Long instanceId) {
        if (isNewRelease(auditModel, instanceId)) {
            createOrUpdateInstanceConfig(auditModel, instanceId);
        }
    }


    private boolean isNewRelease(InstanceConfigRefresh auditModel, Long instanceId) {
        String instanceConfigCacheKey = loadConfigKeyInCacheIfReleaseKeyIsNew(auditModel, instanceId);
        if (instanceConfigCacheKey == null) return false;

        releaseKeyCache.put(instanceConfigCacheKey, auditModel.getReleaseKey());
        return true;
    }

    private void createOrUpdateInstanceConfig(InstanceConfigRefresh auditModel, Long instanceId) {
        //if release key is not the same or cannot find in cache, then do offerHeartBeat
        NamespaceBo namespaceBo = auditModel.getNamespaceBo();
        InstanceConfig instanceConfig = instanceService.findInstanceConfig(instanceId, namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getNamespaceName());

        if (instanceConfig != null) {
            if (ifReleaseKeyIsSameAndRecentBuild(auditModel, instanceConfig)) {
                //when releaseKey is the same, optimize to reduce writes if the record was updated not long ago
                return;
            }

            instanceConfig.setCluster(namespaceBo.getClusterName());
            instanceConfig.setReleaseKey(auditModel.getReleaseKey());
            instanceConfig.setReleaseDeliveryTime(auditModel.getOfferTime());

            //we need to update no matter the release key is the same or not, to ensure the
            //last modified time is updated each day
            instanceConfigRepository.save(instanceConfig);
            return;
        }
        instanceConfig = createInstanceConfig(auditModel, instanceId);

        try {
            instanceService.createInstanceConfig(instanceConfig);
        } catch (DataIntegrityViolationException ex) {
            //concurrent insertion, safe to ignore
        }
    }

    private boolean ifReleaseKeyIsSameAndRecentBuild(InstanceConfigRefresh auditModel, InstanceConfig instanceConfig) {
        return Objects.equals(instanceConfig.getReleaseKey(), auditModel.getReleaseKey()) && offerTimeAndLastModifiedTimeCloseEnough(auditModel.getOfferTime(), instanceConfig.getUpdateTime());
    }


    /**
     * Map instanceConfigCacheKey --> ReleaseKey
     *
     * @param auditModel
     * @param instanceId
     * @return
     */
    private String loadConfigKeyInCacheIfReleaseKeyIsNew(InstanceConfigRefresh auditModel, Long instanceId) {
        //load instance config release key from cache, and check if release key is the same
        String instanceConfigCacheKey = auditModel.assembleInstanceConfigKey(instanceId);

        String cacheReleaseKey = releaseKeyCache.getIfPresent(instanceConfigCacheKey);

        //if release key is the same, then skip offerHeartBeat
        if (cacheReleaseKey != null && Objects.equals(cacheReleaseKey, auditModel.getReleaseKey())) {
            return null;
        }
        return instanceConfigCacheKey;
    }


    private Instance createInstanceByModel(InstanceConfigRefresh auditModel) {
        NamespaceBo namespaceBo = auditModel.getNamespaceBo();
        AppEnvCluster appEnvCluster = appEnvClusterRepository.findByApp_AppCodeAndEnvAndName(namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getClusterName());

        Instance instance = new Instance();
        instance.setAppEnvCluster(appEnvCluster);
        instance.setDataCenter(namespaceBo.getDataCenter());
        instance.setIp(auditModel.getIp());

        try {

            return instanceRepository.save(instance);
        } catch (DataIntegrityViolationException ex) {
            //return the one exists
            return instanceService.findInstance(instance.getAppEnvCluster().getApp().getAppCode(), namespaceBo.getEnv(), instance.getAppEnvCluster().getName(), instance.getDataCenter(), instance.getIp());
        }

    }


    private Long loadFromMemory(String instanceKey) {
        return instanceIdCache.getIfPresent(instanceKey);
    }

    private InstanceConfig createInstanceConfig(InstanceConfigRefresh auditModel, Long instanceId) {
        NamespaceBo namespaceBo = auditModel.getNamespaceBo();
        InstanceConfig instanceConfig = new InstanceConfig();

        AppEnvClusterNamespace namespace = namespaceRepository.findAppEnvClusterNamespace(namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getNamespaceName(), namespaceBo.getClusterName(), "main");

        Instance instance = new Instance(instanceId);
        instanceConfig.setInstance(instance);
        instanceConfig.setNamespace(namespace);
        instanceConfig.setAppCode(namespaceBo.getAppCode());
        instanceConfig.setEnv(namespaceBo.getEnv());
        instanceConfig.setCluster(namespaceBo.getClusterName());
        instanceConfig.setNamespaceName(namespaceBo.getNamespaceName());
        instanceConfig.setReleaseKey(auditModel.getReleaseKey());
        instanceConfig.setReleaseDeliveryTime(auditModel.getOfferTime());
        return instanceConfig;
    }

    private boolean offerTimeAndLastModifiedTimeCloseEnough(LocalDateTime offerTime, LocalDateTime lastModifiedTime) {
//        offerTime. - lastModifiedTime
        Duration duration = Duration.between(offerTime, lastModifiedTime);

        return duration.getSeconds() < OFFER_TIME_LAST_MODIFIED_TIME_THRESHOLD_IN_MILLI;
    }

    private Long loadFromDb(InstanceConfigRefresh auditModel) {
        NamespaceBo namespaceBo = auditModel.getNamespaceBo();
        Instance instance = instanceService.findInstance(namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getClusterName(), namespaceBo.getDataCenter(), auditModel.getIp());
        if (instance != null) {
            return instance.getId();
        }
        return null;
    }

}

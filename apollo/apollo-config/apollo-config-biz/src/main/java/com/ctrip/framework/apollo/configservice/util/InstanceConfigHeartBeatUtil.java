package com.ctrip.framework.apollo.configservice.util;

import com.ctrip.framework.apollo.configservice.InstanceConfigAuditModel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Queues;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.Instance;
import com.yofish.apollo.domain.InstanceConfig;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.yofish.apollo.repository.InstanceRepository;
import com.yofish.apollo.service.InstanceService;
import common.NamespaceBo;
import framework.apollo.core.utils.ApolloThreadFactory;
import framework.apollo.tracer.Tracer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class InstanceConfigHeartBeatUtil implements InitializingBean {
    private static final int INSTANCE_CONFIG_AUDIT_MAX_SIZE = 10000;
    private static final int INSTANCE_CACHE_MAX_SIZE = 50000;
    private static final int INSTANCE_CONFIG_CACHE_MAX_SIZE = 50000;
    private static final long OFFER_TIME_LAST_MODIFIED_TIME_THRESHOLD_IN_MILLI = TimeUnit.MINUTES.toMillis(10);//10 minutes
    private final ExecutorService auditExecutorService;
    private final AtomicBoolean auditStopped;
    private BlockingQueue<InstanceConfigAuditModel> audits = Queues.newLinkedBlockingQueue(INSTANCE_CONFIG_AUDIT_MAX_SIZE);
    private Cache<String, Long> instanceCache;
    private Cache<String, String> instanceConfigReleaseKeyCache;

    @Autowired
    private InstanceService instanceService;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;

    public InstanceConfigHeartBeatUtil() {
        auditExecutorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create("InstanceConfigAuditUtil", true));
        auditStopped = new AtomicBoolean(false);
        instanceCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).maximumSize(INSTANCE_CACHE_MAX_SIZE).build();
        instanceConfigReleaseKeyCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).maximumSize(INSTANCE_CONFIG_CACHE_MAX_SIZE).build();
    }

    public boolean offerHeartBeat(NamespaceBo namespaceBo, String ip, String releaseKey) {
        return this.audits.offer(new InstanceConfigAuditModel(namespaceBo, ip, releaseKey));
    }

    /**
     * 执行心跳
     * 1： 刷新实例
     * 2： 刷新实例配置
     *
     * @param auditModel
     */
    void doHeartBeat(InstanceConfigAuditModel auditModel) {

        Long instanceId = refreshInstance(auditModel);

        refreshInstanceConfig(auditModel, instanceId);
    }

    public Long refreshInstance(InstanceConfigAuditModel auditModel) {
        return createOrUpdateCacheInstanceId(auditModel);
    }

    private void refreshInstanceConfig(InstanceConfigAuditModel auditModel, Long instanceId) {
        if (isNewRelease(auditModel, instanceId)) {
            createOrUpdateInstanceConfig(auditModel, instanceId);
        }
    }



    private boolean isNewRelease(InstanceConfigAuditModel auditModel, Long instanceId) {
        String instanceConfigCacheKey = loadConfigKeyInCacheIfReleaseKeyIsNew(auditModel, instanceId);
        if (instanceConfigCacheKey == null) return false;

        instanceConfigReleaseKeyCache.put(instanceConfigCacheKey, auditModel.getReleaseKey());
        return true;
    }

    private void createOrUpdateInstanceConfig(InstanceConfigAuditModel auditModel, Long instanceId) {
        //if release key is not the same or cannot find in cache, then do offerHeartBeat
        NamespaceBo namespaceBo =  auditModel.getNamespaceBo();
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
            instanceService.updateInstanceConfig(instanceConfig);
            return;
        }
        instanceConfig = createInstanceConfig(auditModel, instanceId);

        try {
            instanceService.createInstanceConfig(instanceConfig);
        } catch (DataIntegrityViolationException ex) {
            //concurrent insertion, safe to ignore
        }
    }

    private boolean ifReleaseKeyIsSameAndRecentBuild(InstanceConfigAuditModel auditModel, InstanceConfig instanceConfig) {
        return Objects.equals(instanceConfig.getReleaseKey(), auditModel.getReleaseKey()) && offerTimeAndLastModifiedTimeCloseEnough(auditModel.getOfferTime(), instanceConfig.getUpdateTime());
    }

    /**
     * Map instanceConfigCacheKey --> ReleaseKey
     *
     * @param auditModel
     * @param instanceId
     * @return
     */
    private String loadConfigKeyInCacheIfReleaseKeyIsNew(InstanceConfigAuditModel auditModel, Long instanceId) {
        //load instance config release key from cache, and check if release key is the same
        String instanceConfigCacheKey = auditModel.assembleInstanceConfigKey(instanceId);

        String cacheReleaseKey = instanceConfigReleaseKeyCache.getIfPresent(instanceConfigCacheKey);

        //if release key is the same, then skip offerHeartBeat
        if (cacheReleaseKey != null && Objects.equals(cacheReleaseKey, auditModel.getReleaseKey())) {
            return null;
        }
        return instanceConfigCacheKey;
    }

    private Long createOrUpdateCacheInstanceId(InstanceConfigAuditModel auditModel) {
        String instanceKey = auditModel.assembleInstanceKey();

        Long instanceId = instanceCache.getIfPresent(instanceKey);
        if (instanceId == null) {
            instanceId = createInstanceIdIfNotInDb(auditModel);
            instanceCache.put(instanceKey, instanceId);
        }
        return instanceId;
    }

    private InstanceConfig createInstanceConfig(InstanceConfigAuditModel auditModel, Long instanceId) {
        NamespaceBo namespaceBo = auditModel.getNamespaceBo();
        InstanceConfig instanceConfig = new InstanceConfig();;
        Instance instance = new Instance(instanceId);
        instanceConfig.setInstance(instance);
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

    private long createInstanceIdIfNotInDb(InstanceConfigAuditModel auditModel) {
        NamespaceBo namespaceBo = auditModel.getNamespaceBo();
        Instance instance = instanceService.findInstance(namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getClusterName(), namespaceBo.getDataCenter(), auditModel.getIp());
        if (instance != null) {
            return instance.getId();
        }
        AppEnvCluster appEnvCluster = appEnvClusterRepository.findByApp_AppCodeAndEnvAndName(namespaceBo.getAppCode(), namespaceBo.getEnv(), namespaceBo.getClusterName());

        instance = new Instance();
        instance.setAppEnvCluster(appEnvCluster);
        instance.setDataCenter(namespaceBo.getDataCenter());
        instance.setIp(auditModel.getIp());

        try {
            return instanceService.createInstance(instance).getId();
        } catch (DataIntegrityViolationException ex) {
            //return the one exists
            return instanceService.findInstance(instance.getAppEnvCluster().getApp().getAppCode(), namespaceBo.getEnv(), instance.getAppEnvCluster().getName(), instance.getDataCenter(), instance.getIp()).getId();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        auditExecutorService.submit(() -> {
            while (!auditStopped.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    InstanceConfigAuditModel model = audits.poll();
                    if (model == null) {
                        TimeUnit.SECONDS.sleep(1);
                        continue;
                    }
                    doHeartBeat(model);
                } catch (Throwable ex) {
                    Tracer.logError(ex);
                }
            }
        });
    }


}

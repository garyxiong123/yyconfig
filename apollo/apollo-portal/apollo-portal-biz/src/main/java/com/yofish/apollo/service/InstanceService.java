package com.yofish.apollo.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Instance;
import com.yofish.apollo.domain.InstanceConfig;
import com.yofish.apollo.repository.InstanceConfigRepository;
import com.yofish.apollo.repository.InstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class InstanceService {
    /*@Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private InstanceConfigRepository instanceConfigRepository;

    public Instance findInstance(String appId, String clusterName, String dataCenter, String ip) {
//        return instanceRepository.findByAppNamespaceAndDataCenterAndIp(new AppNamespace(),dataCenter, ip);
        return null;
    }

    public List<Instance> findInstancesByIds(Set<Long> instanceIds) {
        Iterable<Instance> instances = instanceRepository.findAllById(instanceIds);
        if (instances == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(instances);
    }

    @Transactional
    public Instance createInstance(Instance instance) {
        instance.setId(0L); //protection

        return instanceRepository.save(instance);
    }

    public InstanceConfig findInstanceConfig(long instanceId, String configAppId, String
            configNamespaceName) {
        return instanceConfigRepository
                .findByInstanceIdAndConfigAppIdAndConfigNamespaceName(
                        instanceId, configAppId, configNamespaceName);
    }

    public Page<InstanceConfig> findActiveInstanceConfigsByReleaseKey(String releaseKey, Pageable
            pageable) {
        Page<InstanceConfig> instanceConfigs = instanceConfigRepository
                .findByReleaseKeyAndUpdateTimeAfter(releaseKey,
                        getValidInstanceConfigDate(), pageable);
        return instanceConfigs;
    }

    public Page<Instance> findInstancesByNamespace(String appId, String clusterName, String
            namespaceName, Pageable pageable) {
        Page<InstanceConfig> instanceConfigs = instanceConfigRepository.
                findByConfigAppIdAndConfigClusterNameAndConfigNamespaceNameAndUpdateTimeAfter(appId, clusterName,
                        namespaceName, getValidInstanceConfigDate(), pageable);

        List<Instance> instances = Collections.emptyList();
        if (instanceConfigs.hasContent()) {
//            Set<Long> instanceIds = instanceConfigs.getContent().stream().map
//                    (InstanceConfig::getInstance()).collect(Collectors.toSet());

            Set<Long> instanceIds = null;//fix
            instances = findInstancesByIds(instanceIds);
        }

        return new PageImpl<>(instances, pageable, instanceConfigs.getTotalElements());
    }

    public Page<Instance> findInstancesByNamespaceAndInstanceAppId(String instanceAppId, String
            appId, String clusterName, String
                                                                           namespaceName, Pageable
                                                                           pageable) {
        Page<Object[]> instanceIdResult = instanceConfigRepository
                .findInstanceIdsByNamespaceAndInstanceAppId(instanceAppId, appId, clusterName,
                        namespaceName, getValidInstanceConfigDate(), pageable);

        List<Instance> instances = Collections.emptyList();
        if (instanceIdResult.hasContent()) {
            Set<Long> instanceIds = instanceIdResult.getContent().stream().map((Object o) -> {
                if (o == null) {
                    return null;
                }

                if (o instanceof Integer) {
                    return ((Integer) o).longValue();
                }

                if (o instanceof Long) {
                    return (Long) o;
                }

                //for h2 test
                if (o instanceof BigInteger) {
                    return ((BigInteger) o).longValue();
                }

                return null;
            }).filter((Long value) -> value != null).collect(Collectors.toSet());
            instances = findInstancesByIds(instanceIds);
        }

        return new PageImpl<>(instances, pageable, instanceIdResult.getTotalElements());
    }

    public List<InstanceConfig> findInstanceConfigsByNamespaceWithReleaseKeysNotIn(String appId,
                                                                                   String clusterName,
                                                                                   String
                                                                                           namespaceName,
                                                                                   Set<String>
                                                                                           releaseKeysNotIn) {
        List<InstanceConfig> instanceConfigs = instanceConfigRepository.
                findByConfigAppIdAndConfigClusterNameAndConfigNamespaceNameAndUpdateTimeAfterAndReleaseKeyNotIn(appId, clusterName,
                        namespaceName, getValidInstanceConfigDate(), releaseKeysNotIn);

        if (CollectionUtils.isEmpty(instanceConfigs)) {
            return Collections.emptyList();
        }

        return instanceConfigs;
    }

    *//**
     * Currently the instance config is expired by 1 day, add one more hour to avoid possible time
     * difference
     *//*
    private Date getValidInstanceConfigDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.add(Calendar.HOUR, -1);
        return cal.getTime();
    }

    @Transactional
    public InstanceConfig createInstanceConfig(InstanceConfig instanceConfig) {
        instanceConfig.setId(0L); //protection

        return instanceConfigRepository.save(instanceConfig);
    }

    @Transactional
    public InstanceConfig updateInstanceConfig(InstanceConfig instanceConfig) {
        InstanceConfig existedInstanceConfig = instanceConfigRepository.findById(instanceConfig.getId()).get();
        Preconditions.checkArgument(existedInstanceConfig != null, String.format(
                "Instance config %d doesn't exist", instanceConfig.getId()));

//        existedInstanceConfig.setConfigClusterName(instanceConfig.getConfigClusterName());
//        existedInstanceConfig.setReleaseKey(instanceConfig.getReleaseKey());
//        existedInstanceConfig.setReleaseDeliveryTime(instanceConfig.getReleaseDeliveryTime());
//        existedInstanceConfig.setUpdateTime(instanceConfig
//                .getUpdateTime());

        return instanceConfigRepository.save(existedInstanceConfig);
    }

    @Transactional
    public int batchDeleteInstanceConfig(String configAppId, String configClusterName, String configNamespaceName) {
        return instanceConfigRepository.batchDelete(configAppId, configClusterName, configNamespaceName);
    }*/
}

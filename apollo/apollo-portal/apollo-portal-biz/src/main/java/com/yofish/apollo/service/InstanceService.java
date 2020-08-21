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

import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.api.dto.InstanceConfigDTO;
import com.yofish.apollo.api.dto.InstanceDTO;
import com.yofish.apollo.api.dto.ReleaseDTO;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.yofish.apollo.repository.InstanceConfigRepository;
import com.yofish.apollo.repository.InstanceRepository;
import com.youyu.common.exception.BizException;
import com.youyu.common.utils.YyAssert;
import com.yofish.yyconfig.common.common.dto.PageDTO;
import com.yofish.yyconfig.common.common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class InstanceService {
    private static final Splitter RELEASES_SPLITTER = Splitter.on(",").omitEmptyStrings()
            .trimResults();
    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private InstanceConfigRepository instanceConfigRepository;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    @Autowired
    private AppEnvClusterRepository clusterRepository;

    public Instance findInstance(String appCode, String env, String clusterName, String dataCenter, String ip) {
        AppEnvCluster appEnvCluster = clusterRepository.findByApp_AppCodeAndEnvAndName(appCode, env, clusterName);
        return instanceRepository.findByAppEnvClusterAndDataCenterAndIp(appEnvCluster, dataCenter, ip);
    }

//    @Transactional
//    public Instance createInstance(Instance instance) {
//        instance.setId(0L); //protection
//
//        return instanceRepository.save(instance);
//    }

    /**
     * 获取【使用最新配置】的 实例列表：
     */
    public PageDTO<InstanceDTO> getByRelease(long releaseId4Lastest, Pageable pageable) {

        Release release4Lastest = releaseService.findOne(releaseId4Lastest);

        Page<InstanceConfig> instanceConfigs4Lastest = instanceConfigRepository.findByReleaseKeyAndUpdateTimeAfter(release4Lastest.getReleaseKey(), getValidInstanceConfigDate(), pageable);

        List<InstanceDTO> instanceDTOs = Collections.emptyList();

        PageDTO<InstanceDTO> instanceDTOPageDTO = transformToDTOs(instanceConfigs4Lastest, instanceDTOs, pageable);
        return instanceDTOPageDTO;
    }

    private PageDTO<InstanceDTO> transformToDTOs(Page<InstanceConfig> instanceConfigs4Lastest, List<InstanceDTO> instanceDTOs, Pageable pageable) {
        if (!instanceConfigs4Lastest.hasContent()) {
            return null;
        }
        Multimap<Long, InstanceConfig> instanceConfigMap = HashMultimap.create();
        Set<String> otherReleaseKeys = Sets.newHashSet();

        for (InstanceConfig instanceConfig : instanceConfigs4Lastest.getContent()) {
            instanceConfigMap.put(instanceConfig.getInstance().getId(), instanceConfig);
            otherReleaseKeys.add(instanceConfig.getReleaseKey());
        }

        Set<Long> instanceIds = instanceConfigMap.keySet();

        List<Instance> instances = findInstancesByIds(instanceIds);

        if (!isEmpty(instances)) {
            instanceDTOs = BeanUtils.batchTransform(InstanceDTO.class, instances);
        }

        for (InstanceDTO instanceDTO : instanceDTOs) {
            Collection<InstanceConfig> configs = instanceConfigMap.get(instanceDTO.getId());
            List<InstanceConfigDTO> configDTOs = configs.stream().map(instanceConfig -> {
                InstanceConfigDTO instanceConfigDTO = new InstanceConfigDTO();
                //to save some space
                ReleaseDTO releaseDTO =  createReleaseDTO(instanceConfig);
                instanceConfigDTO.setRelease(releaseDTO);
                instanceDTO.setAppId(instanceConfig.getAppCode());
                instanceDTO.setClusterName(instanceConfig.getCluster());
                instanceDTO.setDataCenter(null);
                instanceConfigDTO.setReleaseDeliveryTime(instanceConfig.getReleaseDeliveryTime());
                instanceConfigDTO.setDataChangeLastModifiedTime(instanceConfig.getUpdateTime());
                return instanceConfigDTO;
            }).collect(Collectors.toList());
            instanceDTO.setConfigs(configDTOs);
        }
        return new PageDTO<>(instanceDTOs, pageable, instanceConfigs4Lastest.getTotalElements());
    }

    private ReleaseDTO createReleaseDTO(InstanceConfig instanceConfig) {
        ReleaseDTO releaseDTO = ReleaseDTO.builder().appId(instanceConfig.getAppCode()).namespaceName(instanceConfig.getNamespaceName()).clusterName(instanceConfig.getCluster()).releaseKey(instanceConfig.getReleaseKey()).build();

        return releaseDTO;
    }


    public InstanceConfig findInstanceConfig(long instanceId, String appCode, String env, String namespaceName) {

        return instanceConfigRepository.findByInstanceIdAndAppCodeAndNamespaceNameAndEnv(instanceId, appCode, namespaceName, env);
    }

    /**
     * 使用的非最新配置的实例
     */
    public List<InstanceDTO> getByReleasesNotIn(Long namspaceId, Set<Long> releaseIdSet) {
        // 获取非最新的实例配置
        List<InstanceConfig> instanceConfigsNotIn = findInstanceConfigNotIn(namspaceId, releaseIdSet);

        return transform2InstanceDTO(instanceConfigsNotIn);
    }

    /**
     * 获取非最新的实例配置
     */
    private List<InstanceConfig> findInstanceConfigNotIn(Long namspaceId, Set<Long> releaseIdSet) {
        Set<String> releaseKeysNotIn = findReleaseKeys4NotIn(releaseIdSet);

        return findInstanceConfigsByNamespaceWithReleaseKeysNotIn(namspaceId, releaseKeysNotIn);
    }

    /**
     * 获取非最新
     */
    private Set<String> findReleaseKeys4NotIn(Set<Long> releaseIdSet) {
        List<Release> releases = releaseService.findByReleaseIds(releaseIdSet);
        YyAssert.assertEmptys(new BizException(String.format("releases not found for %s", releaseIdSet.toString())), releases);
        return releases.stream().map(Release::getReleaseKey).collect(Collectors.toSet());
    }

    private List<InstanceConfig> findInstanceConfigsByNamespaceWithReleaseKeysNotIn(Long namespaceId, Set<String> releaseKeysNotIn) {
        AppEnvClusterNamespace namespace = appEnvClusterNamespaceService.findAppEnvClusterNamespace(namespaceId);

        List<Instance> instances = instanceRepository.findAllByAppEnvCluster(namespace.getAppEnvCluster());
        List<InstanceConfig> instanceConfigs = instanceConfigRepository.findAllByInstanceInAndUpdateTimeAfterAndReleaseKeyNotIn(instances, getValidInstanceConfigDate(), releaseKeysNotIn);

        if (CollectionUtils.isEmpty(instanceConfigs)) {
            return Collections.emptyList();
        }

        return instanceConfigs;
    }

    /**
     * 转化 实例配置 =》 实例DTO
     */
    private List<InstanceDTO> transform2InstanceDTO(List<InstanceConfig> instanceConfigsNotIn) {
        Multimap<Long, InstanceConfig> instanceConfigMap = HashMultimap.create();
        Set<String> otherReleaseKeys = Sets.newHashSet();

        for (InstanceConfig instanceConfig : instanceConfigsNotIn) {
            instanceConfigMap.put(instanceConfig.getInstance().getId(), instanceConfig);
            otherReleaseKeys.add(instanceConfig.getReleaseKey());
        }

        List<Instance> instances = instanceRepository.findInstancesByIdIn(instanceConfigMap.keySet());
        if (isEmpty(instances)) {
            return Collections.emptyList();
        }


        Map<String, ReleaseDTO> releaseMap = buildReleaseMap(otherReleaseKeys);

        List<InstanceDTO> instanceDTOs = BeanUtils.batchTransform(InstanceDTO.class, instances);

        for (InstanceDTO instanceDTO : instanceDTOs) {
            Collection<InstanceConfig> configs = instanceConfigMap.get(instanceDTO.getId());
            List<InstanceConfigDTO> configDTOs = configs.stream().map(instanceConfig -> {
                InstanceConfigDTO instanceConfigDTO = new InstanceConfigDTO();
                instanceConfigDTO.setRelease(releaseMap.get(instanceConfig.getReleaseKey()));
                instanceConfigDTO.setReleaseDeliveryTime(instanceConfig.getReleaseDeliveryTime());
                instanceConfigDTO.setDataChangeLastModifiedTime(instanceConfig.getUpdateTime());
                return instanceConfigDTO;
            }).collect(Collectors.toList());
            instanceDTO.setConfigs(configDTOs);
        }

        return instanceDTOs;
    }

    private Map<String, ReleaseDTO> buildReleaseMap(Set<String> otherReleaseKeys) {
        List<Release> otherReleases = releaseService.findByReleaseKeys(otherReleaseKeys);
        Map<String, ReleaseDTO> releaseMap = Maps.newHashMap();
        for (Release release : otherReleases) {
            //unset configurations to save space
            release.setConfigurations(null);
            ReleaseDTO releaseDTO = BeanUtils.transform(ReleaseDTO.class, release);
            releaseMap.put(release.getReleaseKey(), releaseDTO);
        }
        return releaseMap;
    }


    public Page<InstanceConfig> findActiveInstanceConfigsByReleaseKey(String releaseKey, Pageable
            pageable) {
        Page<InstanceConfig> instanceConfigs = instanceConfigRepository
                .findByReleaseKeyAndUpdateTimeAfter(releaseKey,
                        getValidInstanceConfigDate(), pageable);
        return instanceConfigs;
    }

    public List<Instance> findInstancesByIds(Set<Long> instanceIds) {
        Iterable<Instance> instances = instanceRepository.findAllById(instanceIds);
        if (instances == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(instances);
    }


    /**
     * 默认一天前的
     */
    private LocalDateTime getValidInstanceConfigDate() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(+1).minusHours(+1);
        return dateTime;
    }

    @Transactional
    public InstanceConfig createInstanceConfig(InstanceConfig instanceConfig) {
        instanceConfig.setId(0L); //protection

        return instanceConfigRepository.save(instanceConfig);
    }

//    @Transactional
//    public InstanceConfig updateInstanceConfig(InstanceConfig instanceConfig) {
//        InstanceConfig existedInstanceConfig = instanceConfigRepository.findById(instanceConfig.getId()).orElseGet(() -> {
//            throw new BizException(String.format("instanceId %d doesn't exist", instanceConfig.getId()));
//        });
//
//        existedInstanceConfig.setCluster(instanceConfig.getCluster());
//        existedInstanceConfig.setReleaseKey(instanceConfig.getReleaseKey());
//        existedInstanceConfig.setReleaseDeliveryTime(instanceConfig.getReleaseDeliveryTime());
//
//        return instanceConfigRepository.save(existedInstanceConfig);
//    }

    public int getInstanceCountByNamepsace(Long namespaceId, Pageable pageable) {
        PageDTO<InstanceDTO> instances = findInstancesByNamespace(namespaceId, pageable);
        return (int) instances.getTotal();
    }

    public PageDTO<InstanceDTO> findInstancesByNamespace(Long namespaceId, Pageable pageable) {
        AppEnvClusterNamespace namespace = namespaceRepository.findById(namespaceId).orElseGet(() -> {
            throw new BizException(String.format("namespaceId %d doesn't exist", namespaceId.toString()));
        });
        Page<InstanceConfig> instanceConfigs = namespace.getInstanceConfigs(pageable);
        if (!instanceConfigs.hasContent()) {
            return null;
        }

        List<InstanceDTO> instanceDTOs = new ArrayList<>();
        return transformToDTOs(instanceConfigs, instanceDTOs, pageable);
    }


    public int getInstanceCountByNamepsace(Long namespaceId) {
        AppEnvClusterNamespace namespace = namespaceRepository.findById(namespaceId).orElseGet(() -> {
            throw new BizException(String.format("namespaceId %d doesn't exist", namespaceId.toString()));
        });
        return namespace.calcInstanceConfigsCount();
    }


}

package com.yofish.apollo.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.InstanceConfigDTO;
import com.yofish.apollo.dto.InstanceDTO;
import com.yofish.apollo.dto.ReleaseDTO;
import com.yofish.apollo.repository.InstanceConfigRepository;
import com.yofish.apollo.repository.InstanceRepository;
import com.youyu.common.exception.BizException;

import common.dto.PageDTO;
import common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public PageDTO<InstanceDTO> getByRelease(long releaseId, Pageable pageable) {
        Release release = releaseService.findOne(releaseId);

        if (release == null) {
            throw new BizException(String.format("release not found for %s", releaseId));
        }
        Page<InstanceConfig> instanceConfigsPage = instanceConfigRepository.findByReleaseKeyAndUpdateTimeAfter
                (release.getReleaseKey(),getValidInstanceConfigDate(), pageable);

        List<InstanceDTO> instanceDTOs = Collections.emptyList();

        if (instanceConfigsPage.hasContent()) {
            Multimap<Long, InstanceConfig> instanceConfigMap = HashMultimap.create();
            Set<String> otherReleaseKeys = Sets.newHashSet();

            for (InstanceConfig instanceConfig : instanceConfigsPage.getContent()) {
                instanceConfigMap.put(instanceConfig.getInstance().getId(), instanceConfig);
                otherReleaseKeys.add(instanceConfig.getReleaseKey());
            }

            Set<Long> instanceIds = instanceConfigMap.keySet();

            List<Instance> instances = findInstancesByIds(instanceIds);

            if (!CollectionUtils.isEmpty(instances)) {
                instanceDTOs = BeanUtils.batchTransform(InstanceDTO.class, instances);
            }

            for (InstanceDTO instanceDTO : instanceDTOs) {
                Collection<InstanceConfig> configs = instanceConfigMap.get(instanceDTO.getId());
                List<InstanceConfigDTO> configDTOs = configs.stream().map(instanceConfig -> {
                    InstanceConfigDTO instanceConfigDTO = new InstanceConfigDTO();
                    //to save some space
                    instanceConfigDTO.setRelease(null);
                    instanceConfigDTO.setReleaseDeliveryTime(instanceConfig.getReleaseDeliveryTime());
                    instanceConfigDTO.setDataChangeLastModifiedTime(instanceConfig
                            .getUpdateTime());
                    return instanceConfigDTO;
                }).collect(Collectors.toList());
                instanceDTO.setConfigs(configDTOs);
            }
        }

        return new PageDTO<>(instanceDTOs, pageable, instanceConfigsPage.getTotalElements());
    }


    public List<InstanceDTO> getByReleasesNotIn(Long appEnvClusterNamspace,Set<Long> releaseIdSet) {

        List<Release> releases = releaseService.findByReleaseIds(releaseIdSet);

        if (CollectionUtils.isEmpty(releases)) {
            throw new BizException(String.format("releases not found for %s", releaseIdSet.toString()));
        }

        Set<String> releaseKeys = releases.stream().map(Release::getReleaseKey).collect(Collectors
                .toSet());

        List<InstanceConfig> instanceConfigs =
                findInstanceConfigsByNamespaceWithReleaseKeysNotIn(appEnvClusterNamspace,
                        releaseKeys);

        Multimap<Long, InstanceConfig> instanceConfigMap = HashMultimap.create();
        Set<String> otherReleaseKeys = Sets.newHashSet();

        for (InstanceConfig instanceConfig : instanceConfigs) {
            instanceConfigMap.put(instanceConfig.getInstance().getId(), instanceConfig);
            otherReleaseKeys.add(instanceConfig.getReleaseKey());
        }

        List<Instance> instances = instanceRepository.findInstancesByIdIn(instanceConfigMap.keySet());

        if (CollectionUtils.isEmpty(instances)) {
            return Collections.emptyList();
        }

        List<InstanceDTO> instanceDTOs = BeanUtils.batchTransform(InstanceDTO.class, instances);

        List<Release> otherReleases = releaseService.findByReleaseKeys(otherReleaseKeys);
        Map<String, ReleaseDTO> releaseMap = Maps.newHashMap();

        for (Release release : otherReleases) {
            //unset configurations to save space
            release.setConfigurations(null);
            ReleaseDTO releaseDTO = BeanUtils.transform(ReleaseDTO.class, release);
            releaseMap.put(release.getReleaseKey(), releaseDTO);
        }

        for (InstanceDTO instanceDTO : instanceDTOs) {
            Collection<InstanceConfig> configs = instanceConfigMap.get(instanceDTO.getId());
            List<InstanceConfigDTO> configDTOs = configs.stream().map(instanceConfig -> {
                InstanceConfigDTO instanceConfigDTO = new InstanceConfigDTO();
                instanceConfigDTO.setRelease(releaseMap.get(instanceConfig.getReleaseKey()));
                instanceConfigDTO.setReleaseDeliveryTime(instanceConfig.getReleaseDeliveryTime());
                instanceConfigDTO.setDataChangeLastModifiedTime(instanceConfig
                        .getUpdateTime());
                return instanceConfigDTO;
            }).collect(Collectors.toList());
            instanceDTO.setConfigs(configDTOs);
        }

        return instanceDTOs;
    }

    /*public PageDTO<InstanceDTO> getInstancesByNamespace(Long appEnvClusterNamespaceId, String instanceAppId,
            Pageable pageable) {
        Page<Instance> instances;

        //todo 为什么要区分
       *//* if (Strings.isNullOrEmpty(instanceAppId)) {
            instances = findInstancesByNamespace(appId, clusterName,
                    namespaceName, pageable);
        } else {
            instances = instanceService.findInstancesByNamespaceAndInstanceAppId(instanceAppId, appId,
                    clusterName, namespaceName, pageable);
        }*//*
       instances=findInstancesByNamespace(appEnvClusterNamespaceId,pageable);

        List<Instance> instanceDTOs = BeanUtils.batchTransform(InstanceDTO.class, instances.getContent());
        return new PageDTO<>(instanceDTOs, pageable, instances.getTotalElements());
    }*/

   /* @GetMapping("/by-namespace/count")
    public long getInstancesCountByNamespace(@RequestParam("appId") String appId,
                                             @RequestParam("clusterName") String clusterName,
                                             @RequestParam("namespaceName") String namespaceName) {
        Page<Instance> instances = instanceService.findInstancesByNamespace(appId, clusterName,
                namespaceName, PageRequest.of(0, 1));
        return instances.getTotalElements();
    }*/

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
    public List<InstanceConfig> findInstanceConfigsByNamespaceWithReleaseKeysNotIn(Long appEnvClusterNamespaceId
                                                                                  , Set<String> releaseKeysNotIn) {
        AppEnvClusterNamespace appEnvClusterNamespace=appEnvClusterNamespaceService.findAppEnvClusterNamespace(
                appEnvClusterNamespaceId
        );
        List<Instance> instances=instanceRepository.findAllByAppEnvClusterNamespace(appEnvClusterNamespace);
        List<InstanceConfig> instanceConfigs = instanceConfigRepository.
                findAllByInstanceAndUpdateTimeAfterAndReleaseKeyNotIn(instances,getValidInstanceConfigDate(), releaseKeysNotIn);

        if (CollectionUtils.isEmpty(instanceConfigs)) {
            return Collections.emptyList();
        }

        return instanceConfigs;
    }


    private LocalDateTime getValidInstanceConfigDate() {

        LocalDateTime dateTime=LocalDateTime.now().minusDays(-1).minusHours(-1);
      return dateTime;
    }

}

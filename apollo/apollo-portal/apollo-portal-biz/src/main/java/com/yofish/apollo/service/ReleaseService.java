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

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.api.model.bo.ReleaseBO;
import com.yofish.apollo.api.model.vo.ReleaseCompareResult;
import com.yofish.apollo.repository.*;
import com.youyu.common.exception.BizException;
import com.yofish.yyconfig.common.common.dto.ReleaseDTO;
import com.yofish.yyconfig.common.common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yofish.gary.utils.OrikaCopyUtil.copyProperty4List;

@Service
public class ReleaseService {

    private Gson gson = new Gson();
    @Autowired
    private ReleaseRepository releaseRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private AppNamespaceService namespaceService;
    @Autowired
    private ReleaseHistoryService releaseHistoryService;
    @Autowired
    private Release4MainRepository release4MainRepository;
    @Autowired
    private AppEnvClusterNamespace4MainRepository namespace4MainRepository;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    @Autowired
    private ReleaseMessageRepository messageRepository;


    public Release findActiveOne(long releaseId) {
        return releaseRepository.findByIdAndAbandonedFalse(releaseId);
    }

    public Release findOne(long releaseId) {
        return releaseRepository.findById(releaseId).orElseGet(() -> {
            throw new BizException(String.format("release not found for %s", releaseId));
        });
    }

    public List<Release> findByReleaseIds(Set<Long> releaseIds) {
        Iterable<Release> releases = releaseRepository.findAllById(releaseIds);
        if (releases == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(releases);
    }

    public List<Release> findByReleaseKeys(Set<String> releaseKeys) {
        return releaseRepository.findReleasesByReleaseKeyIn(releaseKeys);
    }


    public List<ReleaseBO> findAllReleases(Long namespaceId, Pageable page) {
        List<Release> releases = releaseRepository.findByAppEnvClusterNamespace_IdOrderByIdDesc(namespaceId, page);
        if (releases == null) {
            return Collections.emptyList();
        }
        return null;
    }


    public ReleaseDTO loadLatestRelease(AppEnvClusterNamespace namespace) {
        Release release = namespace.findLatestActiveRelease();
        return BeanUtils.transform(ReleaseDTO.class, release);
    }


    @Transactional
    public Release publish(AppEnvClusterNamespace namespace, String releaseName, String releaseComment, String operator, boolean isEmergencyPublish) {

        checkLock(namespace, isEmergencyPublish, operator);


        Release release = createRelease(namespace, releaseName, releaseComment, null, isEmergencyPublish);
        Release publishedRelease = release.publish();
        return publishedRelease;
    }

    private boolean hasBranch4Namespace(AppEnvClusterNamespace branchNamespace) {
        return branchNamespace != null;
    }

    private boolean isCurrentReleaseIsBranchRelease(AppEnvClusterNamespace parentNamespace) {
        return hasBranch4Namespace(parentNamespace);
    }

    private void checkLock(AppEnvClusterNamespace namespace, boolean isEmergencyPublish, String operator) {
//    if (!isEmergencyPublish) {
//      AppNamespaceLock lock = namespaceLockService.findLock(appNamespace.getId());
//      if (lock != null && lock.getDataChangeCreatedBy().equals(operator)) {
//        throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Config can not be published by yourself.");
//      }
//    }
    }


    private Release createRelease(AppEnvClusterNamespace namespace, String releaseName, String comment, Map<String, String> configurations, boolean isEmergencyPublish) {
        if (namespace instanceof AppEnvClusterNamespace4Branch) {
            Release4Branch release4Branch = new Release4Branch(namespace, releaseName, comment, configurations, isEmergencyPublish);
            return release4Branch;
        }
        //TODO Fix error
        Release4Main release = new Release4Main(namespace, releaseName, comment, configurations, isEmergencyPublish);


        return release;
    }


    public ReleaseCompareResult compare(long baseReleaseId, long toCompareReleaseId) {

        Release baseRelease = null;
        Release toCompareRelease = null;
        if (baseReleaseId != 0) {
            baseRelease = releaseRepository.findById(baseReleaseId).get();
        }

        if (toCompareReleaseId != 0) {
            toCompareRelease = releaseRepository.findById(toCompareReleaseId).get();
        }
        ReleaseCompareResult compareResult = baseRelease.releaseCompare(toCompareRelease);
        return compareResult;
    }


    public Optional<Release> findReleaseById(long releaseId) {
        return releaseRepository.findById(releaseId);
    }


    public void rollback(long releaseId) {

        Release4Main release4Main = release4MainRepository.findById(releaseId).orElseGet(() -> {
            throw new BizException("12", "release not found");
        });

        release4Main.rollback();
        ReleaseMessage releaseMessage = new ReleaseMessage(release4Main.getAppEnvClusterNamespace());
        messageRepository.save(releaseMessage);
    }


    public List<ReleaseDTO> findActiveReleases(Long namespaceId, Pageable pageable) {
        AppEnvClusterNamespace appEnvClusterNamespace = namespaceRepository.findById(namespaceId).orElseGet(() -> {
            throw new BizException("namespaceId不存在");
        });

        List<Release> latestActiveReleases = appEnvClusterNamespace.findLatestActiveReleases(pageable);

        return transform2Dtos(latestActiveReleases);
    }

    private List<ReleaseDTO> transform2Dtos(List<Release> latestActiveReleases) {
        if (CollectionUtils.isEmpty(latestActiveReleases)) {
            return null;
        }
        List<ReleaseDTO> releaseDTOS = copyProperty4List(latestActiveReleases, ReleaseDTO.class);

//        latestActiveReleases.stream().forEach(release -> {
//            ReleaseDTO releaseDTO = ReleaseDTO.builder().releaseKey(release.)
//        });
        return releaseDTOS;
    }

    public Release findLatestActiveRelease(String appCode, String clusterName, String env, String namespaceName) {
        AppEnvClusterNamespace namespace = namespaceRepository.findAppEnvClusterNamespace(appCode, env, namespaceName, clusterName, "main");
        if (namespace == null) {
            return null;
        }

        return namespace.findLatestActiveRelease();
    }
}

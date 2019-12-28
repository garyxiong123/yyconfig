package com.yofish.apollo.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.enums.ChangeType;
import com.yofish.apollo.model.bo.KVEntity;
import com.yofish.apollo.model.bo.ReleaseBO;
import com.yofish.apollo.model.vo.ReleaseCompareResult;
import com.yofish.apollo.repository.*;
import com.youyu.common.exception.BizException;
import common.constants.GsonType;
import common.dto.ReleaseDTO;
import common.utils.BeanUtils;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yofish.gary.utils.OrikaCopyUtil.copyProperty4List;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
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
        return releaseRepository.findById(releaseId).get();
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
    public Release mergeBranchChangeSetsAndRelease(AppNamespace namespace, String branchName, String releaseName,
                                                   String releaseComment, boolean isEmergencyPublish,
                                                   ItemChangeSets changeSets) {

//        checkLock(namespace, isEmergencyPublish, changeSets.getDataChangeLastModifiedBy());

//        itemSetService.updateSet(namespace, changeSets);
//
//        Release branchRelease = findLatestActiveRelease(namespace.getAppId(), branchName, namespace.getNamespaceName());
//        long branchReleaseId = branchRelease == null ? 0 : branchRelease.getId();
//
//        Map<String, String> operateNamespaceItems = getConfigurations(namespace);
//
//        Map<String, Object> operationContext = Maps.newHashMap();
//        operationContext.put(ReleaseOperationContext.SOURCE_BRANCH, branchName);
//        operationContext.put(ReleaseOperationContext.BASE_RELEASE_ID, branchReleaseId);
//        operationContext.put(ReleaseOperationContext.IS_EMERGENCY_PUBLISH, isEmergencyPublish);
//
//        return masterRelease(namespace, releaseName, releaseComment, operateNamespaceItems, changeSets.getUpdateTime(), ReleaseOperation.GRAY_RELEASE_MERGE_TO_MASTER, operationContext);

        return null;
    }

    @Transactional
    public Release publish(AppEnvClusterNamespace namespace, String releaseName, String releaseComment, String operator, boolean isEmergencyPublish) {

        checkLock(namespace, isEmergencyPublish, operator);


        Release release = createRelease(namespace, releaseName, releaseComment, null, isEmergencyPublish
        );
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


    private Map<String, String> getConfigurations(List<Item> items) {

        if (CollectionUtils.isEmpty(items)) return null;
        Map<String, String> configurations = new HashMap<String, String>();
        for (Item item : items) {
            if (isEmpty(item.getKey())) {
                continue;
            }
            configurations.put(item.getKey(), item.getValue());
        }

        return configurations;
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


    @Transactional
    public int batchDelete(String appId, String clusterName, String namespaceName, String operator) {
        return releaseRepository.batchDelete(appId, clusterName, namespaceName, operator);
    }

    public List<ReleaseBO> findAllReleases(String appId, Env env, String clusterName, String namespaceName, int page, int size) {
        return null;
    }


    public List<ReleaseDTO> findActiveReleases(String appId, Env env, String clusterName, String namespaceName, int page, int size) {
        return null;
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

        return calCompareResult(baseRelease, toCompareRelease);
    }

    private ReleaseCompareResult calCompareResult(Release baseRelease, Release toCompareRelease) {

        Map<String, String> baseReleaseConfiguration = baseRelease == null ? new HashMap<>() : gson.fromJson(baseRelease.getConfigurations(), GsonType.CONFIG);
        Map<String, String> toCompareReleaseConfiguration = toCompareRelease == null ? new HashMap<>() : gson.fromJson(toCompareRelease.getConfigurations(), GsonType.CONFIG);

        ReleaseCompareResult compareResult = new ReleaseCompareResult();

        //added and modified in firstRelease
        for (Map.Entry<String, String> entry : baseReleaseConfiguration.entrySet()) {
            String key = entry.getKey();
            String firstValue = entry.getValue();
            String secondValue = toCompareReleaseConfiguration.get(key);
            //added
            if (secondValue == null) {
                compareResult.addEntityPair(ChangeType.DELETED, new KVEntity(key, firstValue),
                        new KVEntity(key, null));
            } else if (!com.google.common.base.Objects.equal(firstValue, secondValue)) {
                compareResult.addEntityPair(ChangeType.MODIFIED, new KVEntity(key, firstValue),
                        new KVEntity(key, secondValue));
            }

        }

        //deleted in firstRelease
        for (Map.Entry<String, String> entry : toCompareReleaseConfiguration.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (baseReleaseConfiguration.get(key) == null) {
                compareResult
                        .addEntityPair(ChangeType.ADDED, new KVEntity(key, ""), new KVEntity(key, value));
            }

        }

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
        AppEnvClusterNamespace4Main appEnvClusterNamespace4Main = namespace4MainRepository.findById(namespaceId).orElse(null);
        Pageable page = PageRequest.of(0, 2);

        List<Release> latestActiveReleases = appEnvClusterNamespace4Main.findLatestActiveReleases(page);

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
        AppEnvClusterNamespace namespace = namespaceRepository.findAppEnvClusterNamespace(appCode, env,namespaceName, clusterName,"main");
        return namespace.findLatestActiveRelease();
    }
}

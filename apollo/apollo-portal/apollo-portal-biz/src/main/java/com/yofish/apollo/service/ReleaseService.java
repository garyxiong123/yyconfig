package com.yofish.apollo.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.model.bo.ReleaseBO;
import com.yofish.apollo.model.vo.ReleaseCompareResult;
import com.yofish.apollo.repository.Release4MainRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.youyu.common.exception.BizException;
import common.dto.ReleaseDTO;
import common.utils.BeanUtils;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
    //    @Autowired
//    private NamespaceLockService namespaceLockService;
    @Autowired
    private AppNamespaceService namespaceService;
    @Autowired
    private ReleaseHistoryService releaseHistoryService;
    @Autowired
    private Release4MainRepository release4MainRepository;


    public Release findActiveOne(long releaseId) {
        return releaseRepository.findByIdAndAbandonedFalse(releaseId);
    }

    public Release findOne(long releaseId){
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


    public List<ReleaseBO> findAllReleases(String namespaceId, Pageable page) {
//        List<Release> releases = releaseRepository.findByAppIdAndClusterNameAndNamespaceNameOrderByIdDesc(appId,
//                clusterName,
//                namespaceName,
//                page);
//        if (releases == null) {
//            return Collections.emptyList();
//        }
        return null;
    }

    public List<Release> findActiveReleases(String appId, String clusterName, String namespaceName, Pageable page) {
//        List<Release>
//                releases =
//                releaseRepository.findByAppIdAndClusterNameAndNamespaceNameAndIsAbandonedFalseOrderByIdDesc(appId, clusterName,
//                        namespaceName,
//                        page);
//        if (releases == null) {
//            return Collections.emptyList();
//        }
//        return releases;
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


        Release release = createRelease(namespace, releaseComment, releaseComment, null, isEmergencyPublish
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


    private Release createRelease(AppEnvClusterNamespace namespace, String name, String comment, Map<String, String> configurations, boolean isEmergencyPublish) {
        if (namespace instanceof AppEnvClusterNamespace4Branch) {
            Release4Branch release4Branch = new Release4Branch(namespace, name, comment, configurations, isEmergencyPublish);
            return release4Branch;
        }
        //TODO Fix error
        Release4Main release =  new Release4Main(namespace, name, comment, configurations, isEmergencyPublish);


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

    public ReleaseCompareResult compare(Env env, long baseReleaseId, long toCompareReleaseId) {
        return null;
    }

    public Optional<Release> findReleaseById(long releaseId) {
        return releaseRepository.findById(releaseId);
    }


    public void rollback(long releaseId) {

        Release4Main release4Main = release4MainRepository.findById(releaseId).orElseGet( () -> {throw new BizException("12","release not found"); } );

        release4Main.rollback();
    }



    public List<ReleaseDTO> findActiveReleases(String namespaceId, Pageable pageable) {
        return null;
    }
}

package com.yofish.apollo.service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.ReleaseDTO;
import com.yofish.apollo.model.bo.ReleaseBO;
import com.yofish.apollo.model.vo.ReleaseCompareResult;
import com.yofish.apollo.repository.Release4MainRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import common.constants.GsonType;
import common.constants.ReleaseOperation;
import common.exception.NotFoundException;
import framework.apollo.core.enums.Env;
import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yofish.apollo.strategy.CalculateUtil.mergeConfiguration;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class ReleaseService {


    private static final FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");
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
        return releaseRepository.findByIdAndIsAbandonedFalse(releaseId);
    }

    public List<Release> findByReleaseIds(Set<Long> releaseIds) {
        Iterable<Release> releases = releaseRepository.findAllById(releaseIds);
        if (releases == null) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(releases);
    }

    public List<Release> findByReleaseKeys(Set<String> releaseKeys) {
        return releaseRepository.findReleaseByReleaseKeyIn(releaseKeys);
    }


    public List<Release> findAllReleases(String appId, String clusterName, String namespaceName, Pageable page) {
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

        Map<String, String> operateNamespaceItems = getConfigurations(namespace.getItems());

        Release release = createRelease(namespace, releaseComment, releaseComment, operateNamespaceItems, isEmergencyPublish
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
//        throw new BadRequestException("Config can not be published by yourself.");
//      }
//    }
    }

    private void mergeFromMasterAndPublishBranchThenRelease(AppEnvClusterNamespace parentNamespace, AppEnvClusterNamespace childNamespace,
                                                            Map<String, String> parentNamespaceItems,
                                                            String releaseName, String releaseComment,
                                                            String operator, Release masterPreviousRelease,
                                                            Release parentRelease, boolean isEmergencyPublish) {
        //create release for child appNamespace
//        Map<String, String> childReleaseConfiguration = getNamespaceReleaseConfiguration(childNamespace);
//        Map<String, String> parentNamespaceOldConfiguration = masterPreviousRelease == null ? null : gson.fromJson(masterPreviousRelease.getConfigurations(), GsonType.CONFIG);
//
//        Map<String, String> childNamespaceToPublishConfigs = calculateChildNamespaceToPublishConfiguration(parentNamespaceOldConfiguration, parentNamespaceItems, childNamespace);
//
//        //compare
//        if (!childNamespaceToPublishConfigs.equals(childReleaseConfiguration)) {
//            branchRelease(parentNamespace, childNamespace, releaseName, releaseComment,
//                    childNamespaceToPublishConfigs, parentRelease.getId(), operator,
//                    ReleaseOperation.MASTER_NORMAL_RELEASE_MERGE_TO_GRAY, isEmergencyPublish);
//        }

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
        Release4Main release = new Release4Main(namespace, name, comment, configurations, isEmergencyPublish);


        return release;
    }


    public void rollbackChildNamespace(Release release, List<Release> parentNamespaceTwoLatestActiveRelease) {

        Release parentNamespaceNewLatestRelease = parentNamespaceTwoLatestActiveRelease.get(1);

        Release abandonedRelease = parentNamespaceTwoLatestActiveRelease.get(0);
        Map<String, String> parentNamespaceAbandonedConfiguration = gson.fromJson(abandonedRelease.getConfigurations(), GsonType.CONFIG);

        Map<String, String> parentNamespaceNewLatestConfiguration = gson.fromJson(parentNamespaceNewLatestRelease.getConfigurations(), GsonType.CONFIG);

        Map<String, String> childNamespaceNewConfiguration = calculateChildNamespaceToPublishConfiguration(parentNamespaceAbandonedConfiguration, parentNamespaceNewLatestConfiguration, release.getAppEnvClusterNamespace());

//        branchRelease(parentNamespace, childNamespace, TIMESTAMP_FORMAT.format(new Date()) + "-master-rollback-merge-to-gray", "",
//                childNamespaceNewConfiguration, parentNamespaceNewLatestRelease.getId(), operator,
//                ReleaseOperation.MATER_ROLLBACK_MERGE_TO_GRAY, false);
    }

    private Map<String, String> calculateChildNamespaceToPublishConfiguration(Map<String, String> parentNamespaceOldConfiguration, Map<String, String> parentNamespaceNewConfiguration,
            AppEnvClusterNamespace childNamespace) {
        //first. calculate child appNamespace modified configs
        Release childNamespaceLatestActiveRelease = childNamespace.findLatestActiveRelease();

        Map<String, String> childNamespaceLatestActiveConfiguration = childNamespaceLatestActiveRelease == null ? null : gson.fromJson(childNamespaceLatestActiveRelease.getConfigurations(), GsonType.CONFIG);

        Map<String, String> childNamespaceModifiedConfiguration = calculateBranchModifiedItemsAccordingToRelease(parentNamespaceOldConfiguration, childNamespaceLatestActiveConfiguration);

        //second. append child appNamespace modified configs to parent appNamespace new latest configuration
        return mergeConfiguration(parentNamespaceNewConfiguration, childNamespaceModifiedConfiguration);
    }

    private Map<String, String> calculateBranchModifiedItemsAccordingToRelease(
            Map<String, String> masterReleaseConfigs,
            Map<String, String> branchReleaseConfigs) {

        Map<String, String> modifiedConfigs = new HashMap<>();

        if (CollectionUtils.isEmpty(branchReleaseConfigs)) {
            return modifiedConfigs;
        }

        if (CollectionUtils.isEmpty(masterReleaseConfigs)) {
            return branchReleaseConfigs;
        }

        for (Map.Entry<String, String> entry : branchReleaseConfigs.entrySet()) {

            if (!Objects.equals(entry.getValue(), masterReleaseConfigs.get(entry.getKey()))) {
                modifiedConfigs.put(entry.getKey(), entry.getValue());
            }
        }

        return modifiedConfigs;

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

    public ReleaseDTO findReleaseById(Env env, long releaseId) {
        return null;
    }


    public void rollback(Env env, long releaseId) {

        Release4Main release4Main = release4MainRepository.findById(releaseId).get();

        if (release4Main == null) {
            throw new NotFoundException("release not found");
        }
       release4Main.rollback();
    }


}

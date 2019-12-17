package com.yofish.apollo.service;

import com.google.common.collect.Maps;
import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.ReleaseDTO;
import com.yofish.apollo.model.bo.NamespaceBO;
import com.yofish.apollo.repository.AppEnvClusterNamespace4BranchRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.GrayReleaseRuleRepository;
import common.constants.ReleaseOperation;
import common.constants.ReleaseOperationContext;
import common.dto.GrayReleaseRuleDTO;
import common.dto.ItemDTO;
import common.dto.NamespaceDTO;
import common.exception.BadRequestException;
import common.utils.GrayReleaseRuleItemTransformer;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class NamespaceBranchService {

//    @Autowired
//    private ItemsComparator itemsComparator;
    @Autowired
    private AppNamespaceService appNamespaceService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    @Autowired
    private AppEnvClusterNamespace4BranchRepository branchNamespaceRepository;
    @Autowired
    private GrayReleaseRuleRepository grayReleaseRuleRepository;


    @Transactional
    public NamespaceDTO createBranch(Long namespaceId) {
        AppEnvClusterNamespace namespace = namespaceRepository.findById(namespaceId).get();
        AppEnvClusterNamespace4Branch namespace4Branch = (AppEnvClusterNamespace4Branch) creteBranchNamespace(namespace);
        namespaceRepository.save(namespace4Branch);

        return null;

    }

    private AppEnvClusterNamespace creteBranchNamespace(AppEnvClusterNamespace namespace) {
        AppEnvClusterNamespace4Branch appEnvClusterNamespace4Branch = new AppEnvClusterNamespace4Branch(namespace.getAppEnvCluster(), namespace.getAppNamespace());
        return appEnvClusterNamespace4Branch;
    }

    public GrayReleaseRuleDTO findBranchGrayRules(Long namespaceId) {
        AppEnvClusterNamespace4Branch branchNamespace = branchNamespaceRepository.findAppEnvClusterNamespace4BranchByParentId(namespaceId);
        GrayReleaseRule grayReleaseRule = branchNamespace.getGrayReleaseRule();

        GrayReleaseRuleDTO grayReleaseRuleDTO = transform2Dto(grayReleaseRule);
        return grayReleaseRuleDTO;

    }

    private GrayReleaseRuleDTO transform2Dto(GrayReleaseRule grayReleaseRule) {
        return null;
    }

    public void updateBranchGrayRules(String appId, Env env, String clusterName, String namespaceName, String branchName, GrayReleaseRuleDTO rules) {
    }

    public void deleteBranch(String appId, Env env, String clusterName, String namespaceName,
                             String branchName) {

    }


    public ReleaseDTO merge(String appId, Env env, String clusterName, String namespaceName,
                            String branchName, String title, String comment,
                            boolean isEmergencyPublish, boolean deleteBranch) {

        ItemChangeSets changeSets = calculateBranchChangeSet(appId, env, clusterName, namespaceName, branchName);

//        ReleaseDTO mergedResult = releaseService.updateAndPublish(appId, env, clusterName, namespaceName, title, comment, branchName, isEmergencyPublish, deleteBranch, changeSets);
//
//
//        return mergedResult;
        return null;
    }

    private ItemChangeSets calculateBranchChangeSet(String appId, Env env, String clusterName, String namespaceName, String branchName) {
//        NamespaceBO parentNamespace = appNamespaceService.loadNamespaceBO(appId, env, clusterName, namespaceName);
//
//        if (parentNamespace == null) {
//            throw new BadRequestException("base namespace not existed");
//        }
//
//        if (parentNamespace.getItemModifiedCnt() > 0) {
//            throw new BadRequestException("Merge operation failed. Because master has modified items");
//        }
//
//        List<ItemDTO> masterItems = itemService.findItems(appId, env, clusterName, namespaceName);
//
//        List<ItemDTO> branchItems = itemService.findItems(appId, env, branchName, namespaceName);
//
//        ItemChangeSets changeSets = itemsComparator.compareIgnoreBlankAndCommentItem(parentNamespace.getBaseInfo().getId(),
//                masterItems, branchItems);
//        changeSets.setDeleteItems(Collections.emptyList());
//        changeSets.setDataChangeLastModifiedBy(userInfoHolder.getUser().getUserId());
//        return changeSets;
        return null;

    }


    public NamespaceDTO findBranch(String appId, Env env, String clusterName, String namespaceName) {
//        NamespaceDTO namespaceDTO = findBranchBaseInfo(appId, env, clusterName, namespaceName);
//        if (namespaceDTO == null) {
//            return null;
//        }
//        return appNamespaceService.loadNamespaceBO(appId, env, namespaceDTO.getClusterName(), namespaceName);
        return null;
    }


    public AppEnvClusterNamespace4Branch findBranch(Long appId) {
        return appEnvClusterNamespaceService.findChildNamespace(appId);
    }


    @Transactional
    public void updateBranchGrayRules(String appId, String clusterName, String namespaceName,
                                      String branchName, GrayReleaseRule newRules) {
//        doUpdateBranchGrayRules(appId, clusterName, namespaceName, branchName, newRules, true, ReleaseOperation.APPLY_GRAY_RULES);
        return;
    }

    @Transactional
    public GrayReleaseRule updateRulesReleaseId(GrayReleaseRule grayReleaseRule, Release4Branch release4Branch) {

        grayReleaseRule.setRelease(release4Branch);
        grayReleaseRule.setRules(grayReleaseRule.getRules());
        grayReleaseRule.setBranchName(grayReleaseRule.getBranchName());

        grayReleaseRuleRepository.save(grayReleaseRule);


        return grayReleaseRule;
    }

    @Transactional
    public void deleteBranch(Long parentId) {

    }


    private AppEnvClusterNamespace4Branch createNamespaceBranch(Long parentId) {
        AppEnvClusterNamespace4Branch childNamespace = new AppEnvClusterNamespace4Branch(parentId);
        return childNamespace;
    }

}

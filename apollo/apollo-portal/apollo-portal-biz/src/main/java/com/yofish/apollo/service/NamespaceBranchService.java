package com.yofish.apollo.service;

import com.google.gson.Gson;
import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.ReleaseDTO;
import com.yofish.apollo.model.bo.ItemBO;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.yofish.apollo.repository.AppEnvClusterNamespace4BranchRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespace4MainRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.GrayReleaseRuleRepository;
import common.constants.GsonType;
import common.dto.GrayReleaseRuleDTO;
import common.dto.ItemDTO;
import common.dto.NamespaceDTO;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import common.utils.BeanUtils;
import framework.apollo.core.enums.ConfigFileFormat;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private AppEnvClusterNamespace4MainRepository branchNamespaceRepository4Main;
    @Autowired
    private GrayReleaseRuleRepository grayReleaseRuleRepository;
    private Gson gson = new Gson();


    @Transactional
    public NamespaceDTO createBranch(Long namespaceId, String branchName) {
        AppEnvClusterNamespace4Main namespace = (AppEnvClusterNamespace4Main) namespaceRepository.findById(namespaceId).get();
        if (namespace.hasBranchNamespace()) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "namespace already has branch");
        }
        AppEnvClusterNamespace4Branch namespace4Branch = (AppEnvClusterNamespace4Branch) creteBranchNamespace(namespace, branchName);
        namespaceRepository.save(namespace4Branch);
        NamespaceDTO namespaceDTO = transform2Dto(namespace4Branch);
        return namespaceDTO;

    }

    private NamespaceDTO transform2Dto(AppEnvClusterNamespace4Branch namespace4Branch) {
        return null;
    }

    private AppEnvClusterNamespace creteBranchNamespace(AppEnvClusterNamespace namespace, String branchName) {
        AppEnvClusterNamespace4Branch namespace4Branch = new AppEnvClusterNamespace4Branch(namespace.getAppEnvCluster(), namespace.getAppNamespace());
        namespace4Branch.setBranchName(branchName);
        namespace4Branch.setParentId(namespace.getId());
        return namespace4Branch;
    }

    public GrayReleaseRuleDTO findBranchGrayRules(Long namespaceId) {
        AppEnvClusterNamespace4Branch branchNamespace = branchNamespaceRepository.findByParentId(namespaceId);
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
//            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "base namespace not existed");
//        }
//
//        if (parentNamespace.getItemModifiedCnt() > 0) {
//            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Merge operation failed. Because master has modified items");
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


    public NamespaceVO findBranch(Long namespaceId) {
        AppEnvClusterNamespace4Branch branchNamespace = branchNamespaceRepository.findByParentId(namespaceId);
        NamespaceVO namespaceVO = null;
//                transform2BO(branchNamespace);
        return namespaceVO;
    }

//    private NamespaceVO transform2BO(AppEnvClusterNamespace4Branch branchNamespace) {
//        NamespaceVO namespaceVO = new NamespaceVO();
//        Env env = null;
//        namespaceVO.setBaseInfo(null);
//
//        String appId = branchNamespace.getAppId();
//        String clusterName = branchNamespace.getClusterName();
//        String namespaceName = branchNamespace.getNamespaceName();
//
//        fillAppNamespaceProperties(namespaceVO);
//
//        List<ItemBO> itemBOs = new LinkedList<>();
//        namespaceVO.setItems(itemBOs);
//
//        //latest Release
//        Map<String, String> releaseItems = new HashMap<>();
//
//        Release latestRelease = branchNamespace.findLatestActiveRelease();
//        if (latestRelease != null) {
//            releaseItems = gson.fromJson(latestRelease.getConfigurations(), GsonType.CONFIG);
//        }
//
//
//        //not Release config items
//        List<ItemDTO> items = itemService.findItems(appId, env, clusterName, namespaceName);
//        int modifiedItemCnt = 0;
//        for (ItemDTO itemDTO : items) {
//
//            ItemBO itemBO = transformItem2BO(itemDTO, releaseItems);
//
//            if (itemBO.isModified()) {
//                modifiedItemCnt++;
//            }
//
//            itemBOs.add(itemBO);
//        }
//
//        //deleted items
//        List<ItemBO> deletedItems = parseDeletedItems(items, releaseItems);
//        itemBOs.addAll(deletedItems);
//        modifiedItemCnt += deletedItems.size();
//
//        namespaceVO.setItemModifiedCnt(modifiedItemCnt);
//        return null;
//    }
//
//    private List<ItemBO> parseDeletedItems(List<ItemDTO> newItems, Map<String, String> releaseItems) {
//        Map<String, ItemDTO> newItemMap = BeanUtils.mapByKey("key", newItems);
//
//        List<ItemBO> deletedItems = new LinkedList<>();
//        for (Map.Entry<String, String> entry : releaseItems.entrySet()) {
//            String key = entry.getKey();
//            if (newItemMap.get(key) == null) {
//                ItemBO deletedItem = new ItemBO();
//
//                deletedItem.setDeleted(true);
//                ItemDTO deletedItemDto = new ItemDTO();
//                deletedItemDto.setKey(key);
//                String oldValue = entry.getValue();
//                deletedItem.setItem(deletedItemDto);
//
//                deletedItemDto.setValue(oldValue);
//                deletedItem.setModified(true);
//                deletedItem.setOldValue(oldValue);
//                deletedItem.setNewValue("");
//                deletedItems.add(deletedItem);
//            }
//        }
//        return deletedItems;
//    }
//    private ItemBO transformItem2BO(ItemDTO itemDTO, Map<String, String> releaseItems) {
//        String key = itemDTO.getKey();
//        ItemBO itemBO = new ItemBO();
//        itemBO.setItem(itemDTO);
//        String newValue = itemDTO.getValue();
//        String oldValue = releaseItems.get(key);
//        //new item or modified
//        if (!StringUtils.isEmpty(key) && (oldValue == null || !newValue.equals(oldValue))) {
//            itemBO.setModified(true);
//            itemBO.setOldValue(oldValue == null ? "" : oldValue);
//            itemBO.setNewValue(newValue);
//        }
//        return itemBO;
//    }



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

    public GrayReleaseRule updateRulesReleaseId(Release4Branch release4Branch) {
        return null;
    }
}

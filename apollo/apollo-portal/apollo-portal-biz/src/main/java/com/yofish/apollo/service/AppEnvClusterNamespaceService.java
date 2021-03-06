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

import com.google.gson.Gson;
import com.yofish.apollo.api.model.bo.ItemBO;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.enums.NamespaceType;
import com.yofish.apollo.repository.AppEnvClusterNamespace4MainRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.yofish.yyconfig.common.common.constants.GsonType;
import com.yofish.yyconfig.common.common.dto.ItemDTO;
import com.yofish.yyconfig.common.common.dto.NamespaceDTO;
import com.yofish.yyconfig.common.common.dto.ReleaseDTO;
import com.yofish.yyconfig.common.common.utils.BeanUtils;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Service
public class AppEnvClusterNamespaceService {
    @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;
    @Autowired
    private AppEnvClusterNamespace4MainRepository namespace4MainRepository;
    @Autowired
    private AppNamespaceService appNamespaceService;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ReleaseService releaseService;
    private Gson gson = new Gson();

    public AppEnvClusterNamespace findOne(Long namespaceId) {
        return appEnvClusterNamespaceRepository.findById(namespaceId).orElse(null);
    }

    public AppEnvClusterNamespace findOne(String appCode, String env, String clusterName, String namespaceName, AppEnvClusterNamespace.Type type) {
        return appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(appCode, env, namespaceName, clusterName, type.getValue());
    }


    @Transactional
    public void instanceOfAppNamespaces(AppEnvCluster appEnvCluster) {
        List<AppNamespace> appNamespaces = appNamespaceService.findByAppId(appEnvCluster.getApp().getId());

        for (AppNamespace appNamespace : appNamespaces) {
            AppEnvClusterNamespace4Main ns = new AppEnvClusterNamespace4Main(appEnvCluster, appNamespace, null);
            appEnvClusterNamespaceRepository.save(ns);
        }

    }


    public NamespaceVO findPublicNamespaceVoForAssociatedNamespace(String env, String clusterName, String namespaceName) {
        AppEnvClusterNamespace publicNamespaceForAssociatedNamespace = findPublicNamespaceForAssociatedNamespace(env, clusterName, namespaceName);
        NamespaceVO namespaceVO = transformNamespace2VO(publicNamespaceForAssociatedNamespace);
        return namespaceVO;
    }

    public AppEnvClusterNamespace findPublicNamespaceForAssociatedNamespace(String env, String clusterName, String namespaceName) {
        AppNamespace appNamespace = appNamespaceService.findAppNamespace(namespaceName);
        if (appNamespace == null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "namespace not exist");
        }

        String appCode = appNamespace.getApp().getAppCode();

        AppEnvClusterNamespace namespace = findOne(appCode, env, clusterName, namespaceName, AppEnvClusterNamespace.Type.Main);

        //default cluster's namespace
        if (Objects.equals(clusterName, ConfigConsts.CLUSTER_NAME_DEFAULT)) {
            return namespace;
        }

        //custom cluster's namespace not exist.
        //return default cluster's namespace
        if (namespace == null) {
            return findOne(appCode, env, ConfigConsts.CLUSTER_NAME_DEFAULT, namespaceName, AppEnvClusterNamespace.Type.Main);
        }

        //custom cluster's namespace exist and has published.
        //return custom cluster's namespace
        Release latestActiveRelease = namespace.findLatestActiveRelease();
        if (latestActiveRelease != null) {
            return namespace;
        }

        AppEnvClusterNamespace defaultNamespace = findOne(appCode, env, ConfigConsts.CLUSTER_NAME_DEFAULT, namespaceName, AppEnvClusterNamespace.Type.Main);

        //custom cluster's namespace exist but never published.
        //and default cluster's namespace not exist.
        //return custom cluster's namespace
        if (defaultNamespace == null) {
            return namespace;
        }

        //custom cluster's namespace exist but never published.
        //and default cluster's namespace exist and has published.
        //return default cluster's namespace
        Release defaultNamespaceLatestActiveRelease = defaultNamespace.findLatestActiveRelease();
        if (defaultNamespaceLatestActiveRelease != null) {
            return defaultNamespace;
        }

        //custom cluster's namespace exist but never published.
        //and default cluster's namespace exist but never published.
        //return custom cluster's namespace
        return namespace;
    }


    /**
     * load cluster all namespace info with items
     */
    public List<NamespaceVO> findNamespaceVOs(String appCode, String env, String clusterName) {
        AppEnvCluster appEnvCluster = appEnvClusterRepository.findByApp_AppCodeAndEnvAndName(appCode, env, clusterName);
        if (ObjectUtils.isEmpty(appEnvCluster)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "namespaces not exist");
        }
        List<AppEnvClusterNamespace> namespaces = findNamespaces(appEnvCluster);

        List<NamespaceVO> namespaceVOList = new LinkedList<>();
        namespaces.forEach(namespace -> {
            NamespaceVO namespaceVO = transformNamespace2VO(namespace);
            namespaceVOList.add(namespaceVO);
        });

        return namespaceVOList;
    }

    public List<AppEnvClusterNamespace> findNamespaces(AppEnvCluster appEnvCluster) {
        List<AppEnvClusterNamespace> namespaces = namespace4MainRepository.findByAppEnvClusterOrderByIdAsc(appEnvCluster);
        if (namespaces == null) {
            return Collections.emptyList();
        }
        return namespaces;
    }

    private NamespaceDTO transformNamespaceDTO(AppEnvClusterNamespace namespace) {
        NamespaceDTO dto = new NamespaceDTO(
                namespace.getCreateAuthor(),
                namespace.getCreateTime(),
                namespace.getUpdateAuthor(),
                namespace.getUpdateTime(),
                namespace.getId(),
                namespace.getAppEnvCluster().getApp().getAppCode(),
                namespace.getAppEnvCluster().getName(),
                namespace.getAppNamespace().getName(),
                namespace.calcInstanceConfigsCount()
        );
        return dto;
    }

    private NamespaceVO transformNamespace2VO(AppEnvClusterNamespace appEnvClusterNamespace) {
        NamespaceVO namespaceVO = new NamespaceVO();

        NamespaceDTO namespace = transformNamespaceDTO(appEnvClusterNamespace);

        namespaceVO.setBaseInfo(namespace);

        // 处理关联公共命名空间类型的情况
        fillAppNamespaceProperties(namespaceVO);

        List<ItemBO> itemBOs = new LinkedList<>();
        namespaceVO.setItems(itemBOs);

        //latest Release
        ReleaseDTO latestRelease;
        Map<String, String> releaseConfig = new HashMap<>(16);
        Map<String, ItemDTO> deletedItemDTOMap = new HashMap<>(16);

        latestRelease = releaseService.loadLatestRelease(appEnvClusterNamespace);
        if (latestRelease != null) {
            releaseConfig = gson.fromJson(latestRelease.getConfigurations(), GsonType.CONFIG);
        }

        //not Release config items
        List<ItemDTO> items = itemService.findItemsWithoutOrdered(appEnvClusterNamespace.getId()).stream().sorted(Comparator.comparing(Item::getLineNum)).map(item -> transformItemDTO(item)).collect(Collectors.toList());
        int modifiedItemCnt = 0;
        for (ItemDTO itemDTO : items) {

            ItemBO itemBO = transformItem2BO(itemDTO, releaseConfig);

            if (itemBO.isModified()) {
                modifiedItemCnt++;
            }

            itemBOs.add(itemBO);
        }
        //deleted items
        itemService.findDeletedItems(appEnvClusterNamespace.getId()).stream()
                .forEach(item -> deletedItemDTOMap.put(item.getKey(), item));

        List<ItemBO> deletedItems = parseDeletedItems(items, releaseConfig, deletedItemDTOMap);
        itemBOs.addAll(deletedItems);
        modifiedItemCnt += deletedItems.size();

        namespaceVO.setItemModifiedCnt(modifiedItemCnt);

        return namespaceVO;
    }

    /**
     * 这里处理了关联公共命名空间类型的情况
     *
     * @param namespace
     */
    private void fillAppNamespaceProperties(NamespaceVO namespace) {

        NamespaceDTO namespaceDTO = namespace.getBaseInfo();
        //先从当前appId下面找,包含私有的和公共的
        AppNamespace appNamespace = appNamespaceService.findByAppCodeAndName(namespaceDTO.getAppCode(), namespaceDTO.getNamespaceName());
        if (appNamespace != null) {
            namespace.setNamespaceType(NamespaceType.getNamespaceTypeByInstance(appNamespace));
        }
        //再从公共的app namespace里面找,这里找到就是关联了公共命名空间
        if (appNamespace == null) {
            appNamespace = appNamespaceService.findAppNamespace(namespaceDTO.getNamespaceName());

            namespace.setNamespaceType(NamespaceType.Associate);
        }


        if (appNamespace == null) {
            //dirty data
            namespace.setFormat(ConfigFileFormat.Properties.name());
            // set to true, because public namespace allowed to delete by user
            namespace.setNamespaceType(NamespaceType.Public);
        } else {
            namespace.setFormat(appNamespace.getFormat().name());
            namespace.setParentAppCode(appNamespace.getApp().getAppCode());
            namespace.setComment(appNamespace.getComment());
        }
    }

    private List<ItemBO> parseDeletedItems(List<ItemDTO> newItems, Map<String, String> releaseItems, Map<String, ItemDTO> deletedItemDTOs) {
        Map<String, ItemDTO> newItemMap = BeanUtils.mapByKey("key", newItems);

        List<ItemBO> deletedItems = new LinkedList<>();
        for (Map.Entry<String, String> entry : releaseItems.entrySet()) {
            String key = entry.getKey();
            if (newItemMap.get(key) == null) {
                ItemBO deletedItem = new ItemBO();

                deletedItem.setDeleted(true);
                ItemDTO deletedItemDto = deletedItemDTOs.computeIfAbsent(key, k -> new ItemDTO());
                deletedItemDto.setKey(key);
                String oldValue = entry.getValue();
                deletedItem.setItem(deletedItemDto);

                deletedItemDto.setValue(oldValue);
                deletedItem.setModified(true);
                deletedItem.setOldValue(oldValue);
                deletedItem.setNewValue("");
                deletedItems.add(deletedItem);
            }
        }
        return deletedItems;
    }

    private ItemBO transformItem2BO(ItemDTO itemDTO, Map<String, String> releaseItems) {
        String key = itemDTO.getKey();
        ItemBO itemBO = new ItemBO();
        itemBO.setItem(itemDTO);
        String newValue = itemDTO.getValue();
        String oldValue = releaseItems.get(key);
        //new item or modified
        if (!StringUtils.isEmpty(key) && (oldValue == null || !newValue.equals(oldValue))) {
            itemBO.setModified(true);
            itemBO.setOldValue(oldValue == null ? "" : oldValue);
            itemBO.setNewValue(newValue);
        }
        return itemBO;
    }

    private ItemDTO transformItemDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyEntityProperties(item, itemDTO);
        itemDTO.setId(item.getId());
        itemDTO.setNamespaceId(item.getAppEnvClusterNamespace().getId());
        return itemDTO;
    }

    public AppEnvClusterNamespace findAppEnvClusterNamespace(String appCode, String env, String namespace, String cluster, String type) {
        return appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(appCode, env, namespace, cluster, type);
    }

    public AppEnvClusterNamespace findAppEnvClusterNamespace(Long id) {
        return appEnvClusterNamespaceRepository.findAppEnvClusterNamespaceById(id);
    }

    public List<AppEnvClusterNamespace> findbyAppAndEnvAndNamespace(String app, String namespace) {
        return appEnvClusterNamespaceRepository.findbyAppAndEnvAndNamespace(app, namespace);
    }

    public void save(AppEnvClusterNamespace4Main appEnvClusterNamespace) {
        appEnvClusterNamespaceRepository.save(appEnvClusterNamespace);
    }
}

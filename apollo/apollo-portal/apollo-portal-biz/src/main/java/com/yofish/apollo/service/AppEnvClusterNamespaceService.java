package com.yofish.apollo.service;

import com.google.gson.Gson;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.ItemDto;
import com.yofish.apollo.dto.NamespaceListResp;
import com.yofish.apollo.model.bo.ItemBO;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.yofish.apollo.repository.AppEnvClusterNamespace4BranchRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespace4MainRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import common.constants.GsonType;
import common.dto.ItemDTO;
import common.dto.NamespaceDTO;
import common.dto.ReleaseDTO;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import common.utils.BeanUtils;
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
    @Autowired
    private ServerConfigService serverConfigService;
    @Autowired
    private AppEnvClusterNamespace4BranchRepository branchRepository;
    private Gson gson = new Gson();


    public boolean isNamespaceUnique(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        Objects.requireNonNull(appEnvCluster, "appEnvCluster must not be null");
        Objects.requireNonNull(appNamespace, "appNamespace must not be null");
        return Objects.isNull(appEnvClusterNamespaceRepository.findByAppEnvClusterAndAppNamespace(appEnvCluster, appNamespace));
    }

    @Transactional
    public void instanceOfAppNamespaces(AppEnvCluster appEnvCluster) {
        List<AppNamespace> appNamespaces = appNamespaceService.findByAppId(appEnvCluster.getApp().getId());

        for (AppNamespace appNamespace : appNamespaces) {
            AppEnvClusterNamespace4Main ns = new AppEnvClusterNamespace4Main(appEnvCluster, appNamespace);
            appEnvClusterNamespaceRepository.save(ns);
        }

    }

    public void createNamespaceForAppNamespaceInAllCluster(AppNamespace appNamespace) {
        List<AppEnvCluster> appEnvClusters = this.appEnvClusterRepository.findByApp(appNamespace.getApp());
        for (AppEnvCluster appEnvCluster : appEnvClusters) {
            // in case there is some dirty data, e.g. public appNamespace deleted in other app and now created in this app
            if (!this.isNamespaceUnique(appEnvCluster, appNamespace)) {
                continue;
            }

            AppEnvClusterNamespace4Main appEnvClusterNamespace = new AppEnvClusterNamespace4Main(appEnvCluster, appNamespace);
            appEnvClusterNamespaceRepository.save(appEnvClusterNamespace);
        }
    }


    /**
     * load cluster all namespace info with items
     */
    public List<NamespaceVO> findNamespaceVOs(String appCode, String env, String clusterName) {
        AppEnvCluster appEnvCluster = appEnvClusterRepository.findClusterByAppAppCodeAndEnvAndName(appCode, env, clusterName);
        if (ObjectUtils.isEmpty(appEnvCluster)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "namespaces not exist");
        }
        List<AppEnvClusterNamespace> namespaces = findNamespaces(appEnvCluster);

        List<NamespaceVO> namespaceVOList = new LinkedList<>();
        namespaces.forEach(namespace -> {
            NamespaceVO namespaceVO = transformNamespace2BO(namespace);
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
                namespace.getAppNamespace().getApp().getAppCode(),
                namespace.getAppEnvCluster().getName(),
                namespace.getAppNamespace().getName()
        );
        return dto;
    }

    private NamespaceVO transformNamespace2BO(AppEnvClusterNamespace appEnvClusterNamespace) {
        NamespaceVO namespaceVO = new NamespaceVO();

        NamespaceDTO namespace = transformNamespaceDTO(appEnvClusterNamespace);

        namespaceVO.setBaseInfo(namespace);
        namespaceVO.setFormat(appEnvClusterNamespace.getAppNamespace().getFormat().name());
        namespaceVO.setComment(appEnvClusterNamespace.getAppNamespace().getComment());
        namespaceVO.setPublic(appEnvClusterNamespace.getAppNamespace() instanceof AppNamespace4Public);



        List<ItemBO> itemBOs = new LinkedList<>();
        namespaceVO.setItems(itemBOs);

        //latest Release
        ReleaseDTO latestRelease;
        Map<String, String> releaseConfig = new HashMap<>(16);
        Map<String, ItemDTO> deletedItemDTOs = new HashMap<>(16);

        // TODO: 2019-12-21 这个地方还要测试
        latestRelease = releaseService.loadLatestRelease(appEnvClusterNamespace);
        if (latestRelease != null) {
            releaseConfig = gson.fromJson(latestRelease.getConfigurations(), GsonType.CONFIG);
        }

        //not Release config items
        // TODO: 2019-12-21 not release config items
        List<ItemDTO> items = itemService.findItemsWithoutOrdered(appEnvClusterNamespace.getId()).stream().map(item -> transformItemDTO(item)).collect(Collectors.toList());
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
                .forEach(item -> deletedItemDTOs.put(item.getKey(), item));

        List<ItemBO> deletedItems = parseDeletedItems(items, releaseConfig, deletedItemDTOs);
        itemBOs.addAll(deletedItems);
        modifiedItemCnt += deletedItems.size();

        namespaceVO.setItemModifiedCnt(modifiedItemCnt);

        return namespaceVO;
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
        ItemDTO itemDTO=new ItemDTO();
        BeanUtils.copyEntityProperties(item,itemDTO);
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
    public List<AppEnvClusterNamespace> findbyAppAndEnvAndNamespace(String app, String namespace){
        return appEnvClusterNamespaceRepository.findbyAppAndEnvAndNamespace(app,namespace);
    }
}

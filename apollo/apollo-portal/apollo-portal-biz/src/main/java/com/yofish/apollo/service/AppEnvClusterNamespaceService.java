package com.yofish.apollo.service;

import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Main;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.model.bo.ItemBO;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.yofish.apollo.repository.AppEnvClusterNamespace4BranchRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import common.constants.GsonType;
import common.dto.ItemDTO;
import common.dto.NamespaceDTO;
import common.dto.ReleaseDTO;
import common.exception.BadRequestException;
import framework.apollo.core.enums.ConfigFileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Service
public class AppEnvClusterNamespaceService {
    @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;
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
            throw new BadRequestException("namespaces not exist");
        }
        List<AppEnvClusterNamespace> namespaces = findNamespaces(appEnvCluster);

        return null;
    }

    public List<AppEnvClusterNamespace> findNamespaces(AppEnvCluster appEnvCluster) {
        List<AppEnvClusterNamespace> namespaces = appEnvClusterNamespaceRepository.findByAppEnvClusterOrderByIdAsc(appEnvCluster);
        if (namespaces == null) {
            return Collections.emptyList();
        }
        return namespaces;
    }
/*
    private NamespaceVO transformNamespace2BO(String env, NamespaceDTO namespace) {
        NamespaceVO namespaceBO = new NamespaceVO();
        namespaceBO.setBaseInfo(namespace);

        String appId = namespace.getAppId();
        String clusterName = namespace.getClusterName();
        String namespaceName = namespace.getNamespaceName();

        fillAppNamespaceProperties(namespaceBO);

        List<ItemBO> itemBOs = new LinkedList<>();
        namespaceBO.setItems(itemBOs);

        //latest Release
        ReleaseDTO latestRelease;
        Map<String, String> releaseItems = new HashMap<>();
        Map<String, ItemDTO> deletedItemDTOs = new HashMap<>();
        latestRelease = releaseService.loadLatestRelease(appId, env, clusterName, namespaceName);
        if (latestRelease != null) {
            releaseItems = gson.fromJson(latestRelease.getConfigurations(), GsonType.CONFIG);
        }

        //not Release config items
        List<ItemDTO> items = itemService.findItems(appId, env, clusterName, namespaceName);
        int modifiedItemCnt = 0;
        for (ItemDTO itemDTO : items) {

            ItemBO itemBO = transformItem2BO(itemDTO, releaseItems);

            if (itemBO.isModified()) {
                modifiedItemCnt++;
            }

            itemBOs.add(itemBO);
        }

        //deleted items
        itemService.findDeletedItems(appId, env, clusterName, namespaceName).forEach(item -> {
            deletedItemDTOs.put(item.getKey(),item);
        });

        List<ItemBO> deletedItems = parseDeletedItems(items, releaseItems, deletedItemDTOs);
        itemBOs.addAll(deletedItems);
        modifiedItemCnt += deletedItems.size();

        namespaceBO.setItemModifiedCnt(modifiedItemCnt);

        return namespaceBO;
    }

    private void fillAppNamespaceProperties(NamespaceVO namespace) {

        NamespaceDTO namespaceDTO = namespace.getBaseInfo();
        //先从当前appId下面找,包含私有的和公共的
        AppNamespace appNamespace =
                appNamespaceService
                        .findByAppIdAndName(namespaceDTO.getAppId(), namespaceDTO.getNamespaceName());
        //再从公共的app namespace里面找
        if (appNamespace == null) {
            appNamespace = appNamespaceService.findPublicAppNamespace(namespaceDTO.getNamespaceName());
        }

        String format;
        boolean isPublic;
        if (appNamespace == null) {
            //dirty data
            format = ConfigFileFormat.Properties.getValue();
            isPublic = true; // set to true, because public namespace allowed to delete by user
        } else {
            format = appNamespace.getFormat();
            isPublic = appNamespace.isPublic();
            namespace.setParentAppId(appNamespace.getAppId());
            namespace.setComment(appNamespace.getComment());
        }
        namespace.setFormat(format);
        namespace.setPublic(isPublic);
    }*/

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

    public AppEnvClusterNamespace findAppEnvClusterNamespace(String appCode, String env, String namespace, String cluster, String type) {
        return appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(appCode, env, namespace, cluster, type);
    }

    public AppEnvClusterNamespace findAppEnvClusterNamespace(Long id) {
        return appEnvClusterNamespaceRepository.findAppEnvClusterNamespaceById(id);
    }
}

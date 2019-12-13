package com.yofish.apollo.service;

import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.component.txtresolver.ConfigChangeContentBuilder;
import com.yofish.apollo.component.txtresolver.ConfigTextResolver;
import com.yofish.apollo.domain.ClusterNamespace;
import com.yofish.apollo.domain.Commit;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ItemReq;
import com.yofish.apollo.dto.ModifyItemsByTextsReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.apollo.repository.ClusterNamespaceRepository;
import com.yofish.apollo.repository.CommitRepository;
import com.yofish.apollo.repository.ItemRepository;
import common.dto.ItemDTO;
import common.exception.BadRequestException;
import common.exception.NotFoundException;
import common.utils.BeanUtils;
import framework.apollo.core.enums.ConfigFileFormat;
import framework.apollo.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/18 下午2:48
 */

@Service
public class ItemService {

    @Autowired
    private ClusterNamespaceRepository clusterNamespaceRepository;

    @Autowired
    private PortalConfig portalConfig;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    @Qualifier("fileTextResolver")
    private ConfigTextResolver fileTextResolver;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    @Qualifier("propertyResolver")
    private ConfigTextResolver propertyResolver;

    public Item createItem(CreateItemReq createItemReq) {
        Item item = new Item(createItemReq);
        return itemRepository.save(item);
    }


    public void updateItem(UpdateItemReq updateItemReq) {
        Item item = new Item(updateItemReq);
        itemRepository.save(item);

    }
    public void deleteItem(ItemReq deleteItemReq) {

    }
    public List<Item> findItems(ItemReq deleteItemReq) {
        ClusterNamespace clusterNamespace=new ClusterNamespace();
        itemRepository.findFirstByClusterNamespaceOrderByLineNumDesc()
        if (namespace != null) {
            return findItemsWithOrdered(namespace.getId());
        } else {
            return Collections.emptyList();
        }
        return new ArrayList<>();
    }


    public void updateConfigItemByText(ModifyItemsByTextsReq model) {

        String appId = model.getAppId();
        //todo 缺少clusterNamespace
        ClusterNamespace clusterNamespace=new ClusterNamespace();
        String clusterName = model.getClusterName();
        String namespaceName = model.getNamespaceName();
        long namespaceId = model.getNamespaceId();
        String configText = model.getConfigText();

        ConfigTextResolver resolver = findResolver(model.getFormat());

        List<Item> items = itemRepository.findAllByClusterNamespace(clusterNamespace);

        ItemChangeSets changeSets = resolver.resolve(namespaceId, configText, items);
        if (changeSets.isEmpty()) {
            return;
        }
        updateItems(clusterNamespace, changeSets);

    }

    private ConfigTextResolver findResolver(ConfigFileFormat fileFormat) {
        return fileFormat == ConfigFileFormat.Properties ? propertyResolver : fileTextResolver;
    }

    private void updateItems(ClusterNamespace clusterNamespace, ItemChangeSets changeSet) {

        ConfigChangeContentBuilder configChangeContentBuilder = new ConfigChangeContentBuilder();

        if (!CollectionUtils.isEmpty(changeSet.getCreateItems())) {
            for (Item item : changeSet.getCreateItems()) {
                Item createdItem = save(item);
                configChangeContentBuilder.createItem(createdItem);
            }
        }

        if (!CollectionUtils.isEmpty(changeSet.getUpdateItems())) {
            for (Item item : changeSet.getUpdateItems()) {
                Item entity = BeanUtils.transform(Item.class, item);

                Item managedItem = itemService.findOne(entity.getId());
                if (managedItem == null) {
                    throw new NotFoundException(String.format("item not found.(key=%s)", entity.getKey()));
                }
                Item beforeUpdateItem = BeanUtils.transform(Item.class, managedItem);

                //protect. only value,comment,lastModifiedBy,lineNum can be modified
                managedItem.setValue(entity.getValue());
                managedItem.setComment(entity.getComment());
                managedItem.setLineNum(entity.getLineNum());
                managedItem.setDataChangeLastModifiedBy(operator);

                Item updatedItem = itemService.update(managedItem);
                configChangeContentBuilder.updateItem(beforeUpdateItem, updatedItem);

            }

        }

        if (!CollectionUtils.isEmpty(changeSet.getDeleteItems())) {
            for (ItemDTO item : changeSet.getDeleteItems()) {
                Item deletedItem = itemService.delete(item.getId(), operator);
                configChangeContentBuilder.deleteItem(deletedItem);
            }

        }
        if (configChangeContentBuilder.hasContent()){
            Commit commit = Commit.builder().clusterNamespace(clusterNamespace).changeSets(toJSONString(changeSets)).build();
            commitRepository.save(commit);
        }

        return changeSet;
    }
    @Transactional
    public Item delete(long id, String operator) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            throw new IllegalArgumentException("item not exist. ID:" + id);
        }

        item.setDeleted(true);
        item.setDataChangeLastModifiedBy(operator);
        Item deletedItem = itemRepository.save(item);

        auditService.audit(Item.class.getSimpleName(), id, Audit.OP.DELETE, operator);
        return deletedItem;
    }

    @Transactional
    public int batchDelete(long namespaceId, String operator) {
        return itemRepository.deleteByNamespaceId(namespaceId, operator);

    }

    public Item findOne(String appId, String clusterName, String namespaceName, String key) {
        Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
        if (namespace == null) {
            throw new NotFoundException(
                    String.format("namespace not found for %s %s %s", appId, clusterName, namespaceName));
        }
        Item item = itemRepository.findByNamespaceIdAndKey(namespace.getId(), key);
        return item;
    }

    public Item findLastOne(String appId, String clusterName, String namespaceName) {
        Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
        if (namespace == null) {
            throw new NotFoundException(
                    String.format("namespace not found for %s %s %s", appId, clusterName, namespaceName));
        }
        return findLastOne(namespace.getId());
    }

    public Item findLastOne(ClusterNamespace clusterNamespace) {
        return itemRepository.findFirst1ByNamespaceIdOrderByLineNumDesc(namespaceId);
    }

    public Item findOne(long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        return item;
    }

    public List<Item> findItemsWithoutOrdered(Long namespaceId) {
        List<Item> items = itemRepository.findByNamespaceId(namespaceId);
        if (items == null) {
            return Collections.emptyList();
        }
        return items;
    }

    public List<Item> findItemsWithoutOrdered(String appId, String clusterName, String namespaceName) {
        Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
        if (namespace != null) {
            return findItemsWithoutOrdered(namespace.getId());
        } else {
            return Collections.emptyList();
        }
    }

    public List<Item> findItemsWithOrdered(Long namespaceId) {
        List<Item> items = itemRepository.findByNamespaceIdOrderByLineNumAsc(namespaceId);
        if (items == null) {
            return Collections.emptyList();
        }
        return items;
    }

    public List<Item> findItemsWithOrdered(String appId, String clusterName, String namespaceName) {
        Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
        if (namespace != null) {
            return findItemsWithOrdered(namespace.getId());
        } else {
            return Collections.emptyList();
        }
    }

    public List<Item> findItemsModifiedAfterDate(long namespaceId, Date date) {
        return itemRepository.findByNamespaceIdAndDataChangeLastModifiedTimeGreaterThan(namespaceId, date);
    }

    @Transactional
    public Item save(Item entity) {
        checkItemKeyLength(entity.getKey());
        checkItemValueLength(entity.getClusterNamespace().getId(), entity.getValue());

        //entity.setId(0);//protection

        if (entity.getLineNum() == 0) {
            Item lastItem = findLastOne(entity.getClusterNamespace());
            int lineNum = lastItem == null ? 1 : lastItem.getLineNum() + 1;
            entity.setLineNum(lineNum);
        }
        Item item = itemRepository.save(entity);
        return item;
    }

    @Transactional
    public Item update(Item item) {
        checkItemValueLength(item.getNamespaceId(), item.getValue());
        Item managedItem = itemRepository.findById(item.getId()).orElse(null);
        BeanUtils.copyEntityProperties(item, managedItem);
        managedItem = itemRepository.save(managedItem);

        return managedItem;
    }

    private boolean checkItemValueLength(long namespaceId, String value) {
        int limit = getItemValueLengthLimit(namespaceId);
        if (!StringUtils.isEmpty(value) && value.length() > limit) {
            throw new BadRequestException("value too long. length limit:" + limit);
        }
        return true;
    }

    private boolean checkItemKeyLength(String key) {
        if (!StringUtils.isEmpty(key) && key.length() > portalConfig.itemKeyLengthLimit()) {
            throw new BadRequestException("key too long. length limit:" + portalConfig.itemKeyLengthLimit());
        }
        return true;
    }

    private int getItemValueLengthLimit(long namespaceId) {
        Map<Long, Integer> namespaceValueLengthOverride = portalConfig.namespaceValueLengthLimitOverride();
        if (namespaceValueLengthOverride != null && namespaceValueLengthOverride.containsKey(namespaceId)) {
            return namespaceValueLengthOverride.get(namespaceId);
        }
        return portalConfig.itemValueLengthLimit();
    }


}

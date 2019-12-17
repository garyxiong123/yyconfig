package com.yofish.apollo.service;

import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.component.txtresolver.ConfigChangeContentBuilder;
import com.yofish.apollo.component.txtresolver.ConfigTextResolver;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ItemReq;
import com.yofish.apollo.dto.ModifyItemsByTextsReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.CommitRepository;
import com.yofish.apollo.repository.ItemRepository;
import com.youyu.common.exception.BizException;
import common.exception.NotFoundException;
import common.utils.BeanUtils;
import framework.apollo.core.enums.ConfigFileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.toJSON;
import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/18 下午2:48
 */

@Service
public class ItemService {

    @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;

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
    @Autowired
    private CommitService commitService;


    public Item createItem(CreateItemReq createItemReq) {
        Item entity=new Item();
        AppEnvClusterNamespace appEnvClusterNamespace=appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(
                createItemReq.getAppId(),createItemReq.getEnv(),createItemReq.getNamespaceName()
                ,createItemReq.getClusterName(),createItemReq.getType()
        );
        ConfigChangeContentBuilder builder = new ConfigChangeContentBuilder();
        Item managedEntity=  findOne(appEnvClusterNamespace,createItemReq.getKey());
        if (managedEntity != null) {
            throw new BizException("500","item already exists");
        } else {
            Item item = new Item(createItemReq.getKey(),createItemReq.getValue(),createItemReq.getComment(),appEnvClusterNamespace,
                    createItemReq.getLineNum());
            entity=  itemRepository.save(item);
            builder.createItem(entity);
            //添加commit
            commitService.saveCommit(appEnvClusterNamespace,builder.build());
        }
        return entity;
    }


    public void updateItem(UpdateItemReq updateItemReq) {
       /* AppEnvClusterNamespace appEnvClusterNamespace=appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(
                updateItemReq.getAppId(),updateItemReq.getEnv(),updateItemReq.getNamespaceName()
                ,updateItemReq.getClusterName(),updateItemReq.getType()
        );
        Item toUpdateItem = findOne(appEnvClusterNamespace,updateItemReq.getKey());
        if (toUpdateItem == null) {
            throw new NotFoundException(
                    String.format("item not found for %s",toJSONString(updateItemReq)));
        }
        //protect. only value,comment,lastModifiedBy can be modified
        toUpdateItem.setComment(updateItemReq.getComment());
        toUpdateItem.setValue(updateItemReq.getValue());
        itemService.updateItem(appId, Env.fromString(env), clusterName, namespaceName, toUpdateItem);
        itemRepository.save(item);*/
       if(updateItemReq.getItemId()!=null&&updateItemReq.getItemId()>0){
           updateItemById(updateItemReq.getItemId(),updateItemReq.getValue(),updateItemReq.getComment());
       }
    }


    public void updateItemById(Long itemId,String value,String comment) {
        ConfigChangeContentBuilder builder = new ConfigChangeContentBuilder();
        Item managedEntity = findOne(itemId);
        if (managedEntity == null) {
            throw new BizException("item not exist");
        }

        Item beforeUpdateItem = BeanUtils.transform(Item.class, managedEntity);

        //protect. only value,comment,lastModifiedBy can be modified
        managedEntity.setValue(value);
        managedEntity.setComment(comment);

        Item entity = update(managedEntity);
        builder.updateItem(beforeUpdateItem, entity);

        if (builder.hasContent()) {
            commitService.saveCommit(entity.getAppEnvClusterNamespace(),builder.build());
        }

    }


    public void deleteItem(ItemReq deleteItemReq) {
        ConfigChangeContentBuilder builder=new ConfigChangeContentBuilder();
        Item entity = findOne(deleteItemReq.getItemId());
        if (entity == null) {
            throw new BizException("item not found for itemId " + deleteItemReq.getItemId());
        }
        delete(entity.getId());
        builder.deleteItem(entity);
        commitService.saveCommit(entity.getAppEnvClusterNamespace(),builder.build());
    }

    public List<Item> findItems(ItemReq itemReq) {
        AppEnvClusterNamespace appEnvClusterNamespace=new AppEnvClusterNamespace();
        appEnvClusterNamespace.setId(itemReq.getClusterNamespaceId());
           List<Item> items=itemRepository.findAllByAppEnvClusterNamespace(appEnvClusterNamespace);
            if (items != null) {
                return items;
            } else {
                return Collections.emptyList();
            }

    }


    public void updateConfigItemByText(ModifyItemsByTextsReq model) {
        AppEnvClusterNamespace appEnvClusterNamespace=appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(
                model.getAppId(),model.getEnv(),model.getNamespaceName(),model.getClusterName(),model.getType()
        );
        long namespaceId = model.getNamespaceId();
        String configText = model.getConfigText();
        ConfigTextResolver resolver = findResolver(model.getFormat());
        List<Item> items = itemRepository.findAllByAppEnvClusterNamespace(appEnvClusterNamespace);
        ItemChangeSets changeSets = resolver.resolve(namespaceId, configText, items);
        if (changeSets.isEmpty()) {
            return;
        }
        updateItems(appEnvClusterNamespace, changeSets);
        commitService.saveCommit(appEnvClusterNamespace,toJSONString(changeSets));

    }

    private ConfigTextResolver findResolver(ConfigFileFormat fileFormat) {
        return fileFormat == ConfigFileFormat.Properties ? propertyResolver : fileTextResolver;
    }

    private void updateItems(AppEnvClusterNamespace clusterNamespace, ItemChangeSets changeSet) {

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

                Item managedItem = findOne(entity.getId());
                if (managedItem == null) {
                    throw new NotFoundException(String.format("item not found.(key=%s)", entity.getKey()));
                }
                Item beforeUpdateItem = BeanUtils.transform(Item.class, managedItem);

                //protect. only value,comment,lastModifiedBy,lineNum can be modified
                managedItem.setValue(entity.getValue());
                managedItem.setComment(entity.getComment());
                managedItem.setLineNum(entity.getLineNum());
                Item updatedItem = update(managedItem);
                configChangeContentBuilder.updateItem(beforeUpdateItem, updatedItem);
            }

        }

        if (!CollectionUtils.isEmpty(changeSet.getDeleteItems())) {
            for (Item item : changeSet.getDeleteItems()) {
                Item deletedItem = delete(item.getId());
                configChangeContentBuilder.deleteItem(deletedItem);
            }

        }
        if (configChangeContentBuilder.hasContent()) {
            Commit commit = Commit.builder().appEnvClusterNamespace(clusterNamespace).changeSets(toJSONString(changeSet)).build();
            commitRepository.save(commit);
        }

    }

    @Transactional
    public Item delete(long id) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            throw new IllegalArgumentException("item not exist. ID:" + id);
        }
        Item managedItem=new Item();
        BeanUtils.copyEntityProperties(item, managedItem);
        itemRepository.deleteById(id);
        return managedItem;
    }

    @Transactional
    public Item update(Item  item){
        Item managedItem = itemRepository.findById(item.getId()).orElse(null);
        BeanUtils.copyEntityProperties(item, managedItem);
        managedItem = itemRepository.save(managedItem);
        return managedItem;
    }
    @Transactional
    public int batchDelete(long namespaceId, String operator) {
        return 1;

    }
    public Item save(Item item){
        return new Item();
    }

    public Item findOne(Long id){
        return itemRepository.findById(id).get();
    }
    public Item findOne(AppEnvClusterNamespace appEnvClusterNamespace,String key){
        if (appEnvClusterNamespace == null) {
            throw new BizException("500","namespace没有发现");
        }
        Item item = itemRepository.findItemByAppEnvClusterNamespaceAndKey(appEnvClusterNamespace, key);
        return item;
    }


    public Item findOne(String appCode,String env,String namespace,String cluster,String type,String key) {
       AppEnvClusterNamespace appEnvClusterNamespace=appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(
               appCode,env,namespace,cluster,type
       );
       return findOne(appEnvClusterNamespace,key);
    }
    public List<Item> findItemsWithoutOrdered(Long id) {
        return null;
    }

    @Transactional
    public ItemChangeSets updateSet(AppNamespace namespace, ItemChangeSets changeSets){
        return new ItemChangeSets();
    }

    @Transactional
    public ItemChangeSets updateSet(String appId, String clusterName,
                                    String namespaceName, ItemChangeSets changeSet) {

        return new ItemChangeSets();

    }

    private void createCommit(String appId, String clusterName, String namespaceName, String configChangeContent,
                              String operator) {


    }



}



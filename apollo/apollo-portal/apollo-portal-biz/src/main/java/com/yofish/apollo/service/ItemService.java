package com.yofish.apollo.service;

import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.component.txtresolver.ConfigChangeContentBuilder;
import com.yofish.apollo.component.txtresolver.ConfigTextResolver;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ItemReq;
import com.yofish.apollo.dto.ModifyItemsByTextsReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.apollo.model.vo.ItemDiffs;
import com.yofish.apollo.model.vo.NamespaceIdentifier;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.CommitRepository;
import com.yofish.apollo.repository.ItemRepository;
import com.youyu.common.exception.BizException;
import common.dto.ItemDTO;
import common.exception.NotFoundException;
import common.utils.BeanUtils;
import framework.apollo.core.enums.ConfigFileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.toJSON;
import static com.alibaba.fastjson.JSON.toJSONString;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/18 下午2:48
 */

@Service
public class ItemService {

   /* @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;
*/
   @Autowired
   private AppEnvClusterNamespaceService appEnvClusterNamespaceService;
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


    public void createItem(CreateItemReq createItemReq) {

        if(createItemReq.getAppEnvClusterNamespaceIds()!=null
                &&createItemReq.getAppEnvClusterNamespaceIds().size()>0){
           Iterator<Long> iterable=createItemReq.getAppEnvClusterNamespaceIds().iterator();
           while (iterable.hasNext()){
               AppEnvClusterNamespace appEnvClusterNamespace=appEnvClusterNamespaceService.findAppEnvClusterNamespace(iterable.next());

               ConfigChangeContentBuilder builder = new ConfigChangeContentBuilder();
               Item managedEntity=  findOne(appEnvClusterNamespace,createItemReq.getKey());
               if (managedEntity != null) {
                   throw new BizException("500","item already exists");
               } else {
                   Item item = new Item(createItemReq.getKey(),createItemReq.getValue(),createItemReq.getComment(),appEnvClusterNamespace, createItemReq.getLineNum());
                   save(item);
                   Item entity = new Item();
                   BeanUtils.copyEntityProperties(item, entity);
                   builder.createItem(entity);
                   //添加commit
                   commitService.saveCommit(appEnvClusterNamespace,builder.build());
               }
           }


        }


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

    public List<ItemDTO> findDeletedItems(Long appEnvClusterNamespace) {
        List<Commit> commits = commitService.find(appEnvClusterNamespace);
        if (Objects.nonNull(commits)) {
            List<ItemDTO> deletedItems = commits.stream()
                    .map(item -> ConfigChangeContentBuilder.convertJsonString(item.getChangeSets()).getDeleteItems())
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            return deletedItems;
        }
        return Collections.emptyList();
    }

    public List<Item> findItemsWithoutOrdered(ItemReq itemReq){
        AppEnvClusterNamespace appEnvClusterNamespace=new AppEnvClusterNamespace();
        appEnvClusterNamespace.setId(itemReq.getClusterNamespaceId());
       return   findItemsWithoutOrdered(appEnvClusterNamespace);
    }
    public List<Item> findItemsWithoutOrdered(Long id){
        AppEnvClusterNamespace appEnvClusterNamespace=new AppEnvClusterNamespace();
        appEnvClusterNamespace.setId(id);
        return   findItemsWithoutOrdered(appEnvClusterNamespace);
    }
    public List<Item> findItemsWithoutOrdered(AppEnvClusterNamespace appEnvClusterNamespace) {
        List<Item> items=itemRepository.findAllByAppEnvClusterNamespace(appEnvClusterNamespace);
        if (items != null) {
            return items;
        } else {
            return Collections.emptyList();
        }
    }
    public void updateConfigItemByText(ModifyItemsByTextsReq model) {
        AppEnvClusterNamespace appEnvClusterNamespace=appEnvClusterNamespaceService.findAppEnvClusterNamespace(
                model.getAppEnvClusterNamespaceId()
        );
       // long namespaceId = model.getNamespaceId();
        String configText = model.getConfigText();
        ConfigTextResolver resolver = findResolver(model.getFormat());
        List<Item> items = itemRepository.findAllByAppEnvClusterNamespace(appEnvClusterNamespace);
        ItemChangeSets changeSets = resolver.resolve(appEnvClusterNamespace.getId(), configText, items);
        if (changeSets.isEmpty()) {
            return;
        }
        updateSet(appEnvClusterNamespace, changeSets);
        //commitService.saveCommit(appEnvClusterNamespace,toJSONString(changeSets));

    }

    private ConfigTextResolver findResolver(ConfigFileFormat fileFormat) {
        return fileFormat == ConfigFileFormat.Properties ? propertyResolver : fileTextResolver;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSet(AppEnvClusterNamespace namespace, ItemChangeSets changeSet) {

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
            commitService.saveCommit(namespace,configChangeContentBuilder.build());
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
    public Item save(Item entity){
        if (entity.getLineNum()==null ||entity.getLineNum() == 0) {
            Item lastItem = findLastOne(entity.getAppEnvClusterNamespace());
            int lineNum = lastItem == null ? 1 : lastItem.getLineNum() + 1;
            entity.setLineNum(lineNum);
        }

        Item item = itemRepository.save(entity);

        return   itemRepository.save(item);
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
        AppEnvClusterNamespace appEnvClusterNamespace=appEnvClusterNamespaceService.findAppEnvClusterNamespace(
                appCode,env,namespace,cluster,type
        );
        return findOne(appEnvClusterNamespace,key);
    }

    public Item findLastOne(AppEnvClusterNamespace appEnvClusterNamespace) {

        return itemRepository.findFirstByAppEnvClusterNamespaceOrderByLineNumDesc(appEnvClusterNamespace);
    }

    private void createCommit(String appId, String clusterName, String namespaceName, String configChangeContent,
                              String operator) {


    }

    public void syncItems(List<NamespaceIdentifier> comparedNamespaces, List<Item> sourceItems) {
        List<ItemDiffs> itemDiffs = compare(comparedNamespaces, sourceItems);
        for (ItemDiffs itemDiff : itemDiffs) {
            NamespaceIdentifier namespaceIdentifier = itemDiff.getNamespace();
            ItemChangeSets changeSets = itemDiff.getDiffs();
            AppEnvClusterNamespace appEnvClusterNamespace=new AppEnvClusterNamespace();
            appEnvClusterNamespace.setId(namespaceIdentifier.getAppEnvClusterId());
            updateSet(appEnvClusterNamespace, changeSets);

        }
    }
    public List<ItemDiffs> compare(List<NamespaceIdentifier> comparedNamespaces, List<Item> sourceItems) {

        List<ItemDiffs> result = new LinkedList<>();

        for (NamespaceIdentifier namespace : comparedNamespaces) {

            ItemDiffs itemDiffs = new ItemDiffs(namespace);
            try {
                itemDiffs.setDiffs(parseChangeSets(namespace, sourceItems));
            } catch (Exception e) {
                itemDiffs.setDiffs(new ItemChangeSets());
                itemDiffs.setExtInfo("该集群下没有id名为 " + namespace.getAppEnvClusterId() + " 的namespace");
            }
            result.add(itemDiffs);
        }

        return result;
    }

    private ItemChangeSets parseChangeSets(NamespaceIdentifier namespace, List<Item> sourceItems) {
        ItemChangeSets changeSets = new ItemChangeSets();
        AppEnvClusterNamespace appEnvClusterNamespace= appEnvClusterNamespaceService.findAppEnvClusterNamespace(namespace.getAppEnvClusterId());
        List<Item> targetItems =findItemsWithoutOrdered(appEnvClusterNamespace);
        //long namespaceId = getNamespaceId(namespace);
        if (CollectionUtils.isEmpty(targetItems)) {
            //all source items is added
            int lineNum = 1;
            for (Item sourceItem : sourceItems) {
                changeSets.addCreateItem(buildItem(appEnvClusterNamespace, lineNum++, sourceItem));
            }
        } else {
            Map<String, Item> targetItemMap = BeanUtils.mapByKey("key", targetItems);
            String key, sourceValue, sourceComment;
            Item targetItem = null;
            int maxLineNum = targetItems.size();//append to last
            for (Item sourceItem : sourceItems) {
                key = sourceItem.getKey();
                sourceValue = sourceItem.getValue();
                sourceComment = sourceItem.getComment();
                targetItem = targetItemMap.get(key);

                if (targetItem == null) {//added items

                    changeSets.addCreateItem(buildItem(appEnvClusterNamespace, ++maxLineNum, sourceItem));

                } else if (isModified(sourceValue, targetItem.getValue(), sourceComment,
                        targetItem.getComment())) {//modified items
                    targetItem.setValue(sourceValue);
                    targetItem.setComment(sourceComment);
                    changeSets.addUpdateItem(targetItem);
                }
            }
        }

        return changeSets;
    }

    private Item buildItem(AppEnvClusterNamespace namespace, int lineNum, Item sourceItem) {
        Item createdItem = new Item();
        BeanUtils.copyEntityProperties(sourceItem, createdItem);
        createdItem.setLineNum(lineNum);
        createdItem.setAppEnvClusterNamespace(namespace);
        return createdItem;
    }
    private boolean isModified(String sourceValue, String targetValue, String sourceComment, String targetComment) {

        if (!sourceValue.equals(targetValue)) {
            return true;
        }

        if (sourceComment == null) {
            return !StringUtils.isEmpty(targetComment);
        } else if (targetComment != null) {
            return !sourceComment.equals(targetComment);
        } else {
            return false;
        }
    }



}



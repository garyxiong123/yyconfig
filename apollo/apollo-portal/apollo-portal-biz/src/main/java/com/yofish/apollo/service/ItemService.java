package com.yofish.apollo.service;

import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.component.txtresolver.ConfigChangeContentBuilder;
import com.yofish.apollo.component.txtresolver.ConfigTextResolver;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Commit;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ItemReq;
import com.yofish.apollo.dto.ModifyItemsByTextsReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.CommitRepository;
import com.yofish.apollo.repository.ItemRepository;
import common.exception.NotFoundException;
import common.utils.BeanUtils;
import framework.apollo.core.enums.ConfigFileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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

    public List<Item> findItems(ItemReq itemReq) {


        return new ArrayList<>();
    }


    public void updateConfigItemByText(ModifyItemsByTextsReq model) {

        String appId = model.getAppId();
        //todo 缺少clusterNamespace
        AppEnvClusterNamespace appEnvClusterNamespace = new AppEnvClusterNamespace();
        String clusterName = model.getClusterName();
        String namespaceName = model.getNamespaceName();
        long namespaceId = model.getNamespaceId();
        String configText = model.getConfigText();

        ConfigTextResolver resolver = findResolver(model.getFormat());

        List<Item> items = itemRepository.findAllByAppEnvClusterNamespace(appEnvClusterNamespace);

        ItemChangeSets changeSets = resolver.resolve(namespaceId, configText, items);
        if (changeSets.isEmpty()) {
            return;
        }

        updateItems(appEnvClusterNamespace, changeSets);

        Commit commit = Commit.builder().appEnvClusterNamespace(appEnvClusterNamespace).changeSets(toJSONString(changeSets)).build();
        commitRepository.save(commit);

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
        return new Item();
    }
    public Item update(Item  item){
        return new Item();
    }
    @Transactional
    public int batchDelete(long namespaceId, String operator) {
        return 1;

    }
    public Item save(Item item){
        return new Item();
    }

    public Item findOne(Long id){
        return new Item();
    }

}



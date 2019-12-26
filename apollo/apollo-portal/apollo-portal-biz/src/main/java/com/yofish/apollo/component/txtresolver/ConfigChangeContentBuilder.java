package com.yofish.apollo.component.txtresolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.dto.ItemPair;
import common.dto.ItemDTO;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author rache
 * @date 2019-12-11
 */
@Data
public class ConfigChangeContentBuilder {

    private List<ItemDTO> createItems = new LinkedList<>();
    private List<ItemPair> updateItems = new LinkedList<>();
    private List<ItemDTO> deleteItems = new LinkedList<>();


    public ConfigChangeContentBuilder createItem(Item item) {
        if (!StringUtils.isEmpty(item.getKey())){
            createItems.add(cloneItem(item));
        }
        return this;
    }

    public ConfigChangeContentBuilder updateItem(Item oldItem, Item newItem) {
        if (!oldItem.getValue().equals(newItem.getValue())){
            ItemPair itemPair = new ItemPair(cloneItem(oldItem), cloneItem(newItem));
            updateItems.add(itemPair);
        }
        return this;
    }

    public ConfigChangeContentBuilder deleteItem(Item item) {
        if (!StringUtils.isEmpty(item.getKey())) {
            deleteItems.add(cloneItem(item));
        }
        return this;
    }

    public boolean hasContent(){
        return !createItems.isEmpty() || !updateItems.isEmpty() || !deleteItems.isEmpty();
    }

    public String build() {
        //因为事务第一段提交并没有更新时间,所以build时统一更新
        /*Date now = new Date();

        for (Item item : createItems) {
            item.set(now);
        }

        for (ItemPair item : updateItems) {
            item.newItem.set(now);
        }

        for (Item item : deleteItems) {
            item.setDataChangeLastModifiedTime(now);
        }*/
        return JSONObject.toJSONString(this);

    }



    ItemDTO cloneItem(Item item) {
       /* Item target = new Item();

        BeanUtils.copyProperties(source, target);

        return target;*/
        ItemDTO itemDto= new ItemDTO();
        BeanUtils.copyProperties(item,itemDto);
        itemDto.setNamespaceId(item.getAppEnvClusterNamespace().getId());
        return itemDto;
    }

    public static ConfigChangeContentBuilder convertJsonString(String content) {
        ConfigChangeContentBuilder json= JSON.parseObject(content, ConfigChangeContentBuilder.class);
        return json;
    }


}

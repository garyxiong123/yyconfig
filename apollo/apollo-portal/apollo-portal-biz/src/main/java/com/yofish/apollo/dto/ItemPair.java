package com.yofish.apollo.dto;

import common.dto.ItemDTO;
import lombok.Data;

/**
 * @author rache
 * @date 2019-12-26
 */
@Data
public class ItemPair {
    ItemDTO oldItem;
    ItemDTO newItem;
    public ItemPair(){

    }

    public ItemPair(ItemDTO oldItem, ItemDTO newItem) {
        this.oldItem = oldItem;
        this.newItem = newItem;
    }
}

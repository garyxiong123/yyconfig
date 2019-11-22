package com.yofish.apollo.bo;


import com.yofish.apollo.domain.Item;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * storage cud result
 */
@Data
public class ItemChangeSets {

    private List<Item> createItems = new LinkedList<>();
    private List<Item> updateItems = new LinkedList<>();
    private List<Item> deleteItems = new LinkedList<>();

    public void addCreateItem(Item item) {
        createItems.add(item);
    }

    public void addUpdateItem(Item item) {
        updateItems.add(item);
    }

    public void addDeleteItem(Item item) {
        deleteItems.add(item);
    }

    public boolean isEmpty() {
        return createItems.isEmpty() && updateItems.isEmpty() && deleteItems.isEmpty();
    }


}

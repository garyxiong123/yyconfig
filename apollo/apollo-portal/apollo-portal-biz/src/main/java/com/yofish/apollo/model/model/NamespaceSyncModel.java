package com.yofish.apollo.model.model;

import com.yofish.apollo.domain.Item;
import com.yofish.apollo.model.vo.NamespaceIdentifier;
import common.dto.ItemDTO;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class NamespaceSyncModel {

    private List<NamespaceIdentifier> syncToNamespaces;
    private List<Item> syncItems;


    public List<NamespaceIdentifier> getSyncToNamespaces() {
        return syncToNamespaces;
    }

    public void setSyncToNamespaces(List<NamespaceIdentifier> syncToNamespaces) {
        this.syncToNamespaces = syncToNamespaces;
    }

    public List<Item> getSyncItems() {
        return syncItems;
    }

    public void setSyncItems(List<Item> syncItems) {
        this.syncItems = syncItems;
    }
}

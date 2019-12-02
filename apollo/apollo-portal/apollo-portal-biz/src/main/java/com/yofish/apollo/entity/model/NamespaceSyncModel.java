package com.yofish.apollo.entity.model;

import com.yofish.apollo.entity.vo.NamespaceIdentifier;
import common.dto.ItemDTO;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class NamespaceSyncModel implements Verifiable {

    private List<NamespaceIdentifier> syncToNamespaces;
    private List<ItemDTO> syncItems;

    @Override
    public boolean isInvalid() {
        if (CollectionUtils.isEmpty(syncToNamespaces) || CollectionUtils.isEmpty(syncItems)) {
            return true;
        }
        for (NamespaceIdentifier namespaceIdentifier : syncToNamespaces) {
            if (namespaceIdentifier.isInvalid()) {
                return true;
            }
        }
        return false;
    }

    public List<NamespaceIdentifier> getSyncToNamespaces() {
        return syncToNamespaces;
    }

    public void setSyncToNamespaces(List<NamespaceIdentifier> syncToNamespaces) {
        this.syncToNamespaces = syncToNamespaces;
    }

    public List<ItemDTO> getSyncItems() {
        return syncItems;
    }

    public void setSyncItems(List<ItemDTO> syncItems) {
        this.syncItems = syncItems;
    }
}

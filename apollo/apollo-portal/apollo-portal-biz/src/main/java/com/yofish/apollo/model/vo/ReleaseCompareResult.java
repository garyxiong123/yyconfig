package com.yofish.apollo.model.vo;


import com.yofish.apollo.model.bo.KVEntity;
import com.yofish.apollo.enums.ChangeType;
import common.entity.EntityPair;

import java.util.LinkedList;
import java.util.List;

public class ReleaseCompareResult {

    private List<Change> changes = new LinkedList<>();

    public void addEntityPair(ChangeType type, KVEntity firstEntity, KVEntity secondEntity) {
        changes.add(new Change(type, new EntityPair<>(firstEntity, secondEntity)));
    }

    public boolean hasContent() {
        return !changes.isEmpty();
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

}

package com.yofish.apollo.model.vo;


import com.yofish.apollo.model.bo.KVEntity;
import com.yofish.apollo.enums.ChangeType;
import common.entity.EntityPair;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

public class Change {


    @ApiModelProperty("类型 ADDED, MODIFIED, DELETED")
    private ChangeType type;
    private EntityPair<KVEntity> entity;

    public Change(ChangeType type, EntityPair<KVEntity> entity) {
        this.type = type;
        this.entity = entity;
    }

    public ChangeType getType() {
        return type;
    }

    public void setType(ChangeType type) {
        this.type = type;
    }

    public EntityPair<KVEntity> getEntity() {
        return entity;
    }

    public void setEntity(EntityPair<KVEntity> entity) {
        this.entity = entity;
    }
}

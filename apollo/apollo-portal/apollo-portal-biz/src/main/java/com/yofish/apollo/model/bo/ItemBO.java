package com.yofish.apollo.model.bo;


import common.dto.ItemDTO;
import lombok.Data;

@Data
public class ItemBO {
    private ItemDTO item;
    private boolean isModified;
    private boolean isDeleted;
    private String oldValue;
    private String newValue;
}

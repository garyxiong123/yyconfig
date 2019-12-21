package com.yofish.apollo.model.bo;


import common.dto.ItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemBO {
    private ItemDTO item;
    private boolean isModified;
    private boolean isDeleted;
    private String oldValue;
    private String newValue;
}

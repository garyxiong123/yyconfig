package com.yofish.apollo.dto;

import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.component.txtresolver.ConfigChangeContentBuilder;
import com.yofish.apollo.model.vo.NamespaceIdentifier;
import common.dto.ItemDTO;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author rache
 * @date 2020-01-02
 */
@Data
public class ItemChangeSetDto {
    private NamespaceIdentifier namespace;
    private ConfigChangeContentBuilder diffs;
    private String extInfo;

}

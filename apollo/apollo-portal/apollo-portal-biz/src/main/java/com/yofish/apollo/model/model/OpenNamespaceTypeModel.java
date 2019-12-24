package com.yofish.apollo.model.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author WangSongJun
 * @date 2019-12-24
 */
@Data
public class OpenNamespaceTypeModel {

    @NotBlank(message = "name cannot be blank")
    private String name;

    private String comment;
}

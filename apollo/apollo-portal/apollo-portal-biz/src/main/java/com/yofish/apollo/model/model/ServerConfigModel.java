package com.yofish.apollo.model.model;

import com.yofish.apollo.config.ServerConfigKey;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author WangSongJun
 * @date 2020-01-06
 */
@Data
public class ServerConfigModel {
    @NotNull(message = "key  cannot be null")
    private ServerConfigKey key;


    @NotBlank(message = "value cannot be blank")
    private String value;


    @NotBlank(message = "comment cannot be blank")
    private String comment;
}

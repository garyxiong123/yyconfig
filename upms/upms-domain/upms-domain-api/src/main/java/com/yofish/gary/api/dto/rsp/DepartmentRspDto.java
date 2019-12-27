package com.yofish.gary.api.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author WangSongJun
 * @date 2019-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("部门响应rsp")
public class DepartmentRspDto implements Serializable {

    @ApiModelProperty("ID")
    private long id;

    @ApiModelProperty("code")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("备注")
    private String comment;
}
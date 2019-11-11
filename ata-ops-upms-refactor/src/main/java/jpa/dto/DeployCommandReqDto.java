package jpa.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:14
 */
@Data
public class DeployCommandReqDto {


    @NotNull
    @ApiModelProperty("操作事件类型")
    private String type;

    @NotNull
    @ApiModelProperty("服务Id")
    private Long appId;

    @NotNull
    @ApiModelProperty("环境id")
    private Long envId;

    @NotNull
    @ApiModelProperty("操作事件的参数(Json格式)")
    private String detail;

    private String branch;


    private Long deployConfigId;

}

/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.gary.api.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户编辑请求req
 */
@ApiModel("用户编辑请求req")
@Setter
@Getter
public class UserEditReqDTO implements Serializable {

    private static final long serialVersionUID = -7395198796852193687L;

    @ApiModelProperty("用户id")
    @NotNull
    private Long id;

    @ApiModelProperty("真实姓名")
    @Size(max = 32)
    @Pattern(regexp = "^[a-zA-Z\\u4e00-\\u9fa5\\pP]{1,32}$")
    private String realName;

    @ApiModelProperty("密码")
    @Size(min = 6, max = 14)
    @Pattern(regexp = "^[a-zA-Z0-9\\pP]{6,14}$")
    private String password;

    @ApiModelProperty("性别(0:男 1:女)")
    private Integer sex;

    @ApiModelProperty("手机号")
    @Pattern(regexp = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$", message = "请输入正确的手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    @Email(message = "请输入正确的邮箱")
    @Size(min = 6, max = 100)
    private String email;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态(0:有效 1:冻结)")
    private String status;

    @ApiModelProperty("部门ID")
    private Long departmentId;

    @ApiModelProperty("角色id列表")
    private List<Long> roleIds;
}

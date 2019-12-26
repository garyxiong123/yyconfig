package com.yofish.gary.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author WangSongJun
 * @date 2019-12-09
 */
@Data
public class DepartmentModel {

    @NotBlank(message = "code cannot be blank")
    @Pattern(
            regexp = "[0-9a-zA-Z_.-]+",
            message = "Invalid code format: 只允许输入数字，字母和符号 - _ ."
    )
    private String code;

    @NotBlank(message = "name cannot be blank")
    private String name;

    private String comment;
}

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
package com.yofish.gary.api.enums;

import com.youyu.common.api.IBaseResultCode;
import lombok.Getter;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月28日 10:00:00
 * @work 错误码枚举
 */
@Getter
public enum UpmsResultCode implements IBaseResultCode {

    USER_STATUS_INVALID("0001", "用户状态无效"),
    CONFIRM_PASSWORD_MUST_EQUAL("0002", "确认密码必须和密码相等"),
    USERNAME_OR_EMAIL_ALREADY_EXIST("0003", "用户名或者邮箱已经存在"),
    USERNAME_OR_PASSWORD_ERROR("0004", "用户名或密码错误"),
    WRONG_PASSWORD("0005", "密码错误"),
    LOGIN_EXCEPTION("0006", "登录异常"),
    UNAUTHORIZED_ACCESS("0007", "非法访问"),
    ACCESS_EXCEPTION_NEED_LOGIN("0008", "访问异常需登录"),
    ROLE_NAME_ALREADY_EXIST("0009", "角色名已经存在"),
    USER_UNAUTHORIZED("0010", "用户未授权"),
    USER_SESSION_EXPIRED("0011", "用户会话已过期"),
    SESSION_USER_ID_IS_NULL("0012", "会话对应用户id为空"),
    USER_NOT_EXIST("0013", "用户不存在"),
    EMAIL_ALREADY_EXIST("0014", "用户邮箱已经存在"),
    DEPARTMENT_NOT_EXIST("0015", "部门不存在");

    /**
     * 错误码前缀
     */
    public static final String RESULT_CODE_PREFIX = "0103";

    private String code;
    private String desc;

    UpmsResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return RESULT_CODE_PREFIX + code;
    }
}

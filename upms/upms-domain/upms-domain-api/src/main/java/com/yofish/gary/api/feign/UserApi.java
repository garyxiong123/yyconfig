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
package com.yofish.gary.api.feign;

import com.yofish.gary.api.dto.req.*;
import com.yofish.gary.api.dto.rsp.UserDetailRspDTO;
import com.yofish.gary.api.dto.rsp.UserLoginRspDTO;
import com.yofish.gary.api.dto.rsp.UserMenuPermissionRspDTO;
import com.yofish.gary.api.dto.rsp.UserQueryRspDTO;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户Api
 */
@RequestMapping("/user")
public interface UserApi {

    /**
     * 登录
     *
     * @param userLoginReqDTO
     * @return
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    Result<UserLoginRspDTO> login(@Valid @RequestBody UserLoginReqDTO userLoginReqDTO);

    /**
     * 登出
     *
     * @return
     */
    @ApiOperation("登出")
    @PostMapping("/logout")
    Result logout();

    /**
     * 修改密码
     *
     * @param userModifyPasswordReqDTO
     * @return
     */
    @ApiOperation("修改密码")
    @PostMapping("/modifyPassword")
    Result modifyPassword(@Valid @RequestBody UserModifyPasswordReqDTO userModifyPasswordReqDTO);

    /**
     * 添加用户
     *
     * @param userAddReqDTO
     * @return
     */
    @ApiOperation("添加用户")
    @PostMapping("/add")
    Result<Long> add(@Valid @RequestBody UserAddReqDTO userAddReqDTO);

    /**
     * 删除用户
     *
     * @param userDeleteReqDTO
     * @return
     */
    @ApiOperation("删除用户")
    @PostMapping("/delete")
    Result delete(@Valid @RequestBody UserDeleteReqDTO userDeleteReqDTO);

    /**
     * 编辑用户
     *
     * @param userEditReqDTO
     * @return
     */
    @ApiOperation("编辑用户")
    @PostMapping("/edit")
    Result edit(@Valid @RequestBody UserEditReqDTO userEditReqDTO);

    /**
     * 查询分页用户
     *
     * @param userQueryReqDTO
     * @return
     */
    @ApiOperation("查询分页用户")
    @PostMapping("/getPage")
    Result<PageData<UserQueryRspDTO>> getPage(@RequestBody UserQueryReqDTO userQueryReqDTO);

    /**
     * 查询用户列表
     *
     * @param userQueryReqDTO
     * @return
     */
    @ApiOperation("查询用户列表")
    @PostMapping("/getList")
    Result<List<UserQueryRspDTO>> getList(@RequestBody UserQueryReqDTO userQueryReqDTO);

    /**
     * 未授权访问处理
     *
     * @return
     */
    @ApiOperation("未授权访问处理")
    @GetMapping("/unauthorized")
    Result unauthorized();

    /**
     * 未登录访问处理
     *
     * @return
     */
    @ApiOperation("未登录访问处理")
    @GetMapping("/needLogin")
    Result needLogin();

    /**
     * 获取用户菜单权限列表
     *
     * @param userMenuPermissionReqDTO
     * @return
     */
    @ApiOperation("获取用户菜单权限列表")
    @PostMapping("/getUserMenuPermissions")
    Result<List<UserMenuPermissionRspDTO>> getUserMenuPermissions(@RequestBody UserMenuPermissionReqDTO userMenuPermissionReqDTO);

    /**
     * 根据用户id查询用户详细信息
     *
     * @param userId
     * @return
     */
    @ApiOperation("根据用户id查询用户详细信息")
    @PostMapping("/getUserDetail")
    Result<UserDetailRspDTO> getUserDetail(@RequestBody Long userId);

    /**
     * 根据用户id判断用户是否是管理员
     *
     * @param userId
     * @return
     */
    @ApiOperation("根据用户id判断用户是否是管理员")
    @PostMapping("/isAdmin")
    Result<Boolean> isAdmin(@RequestBody Long userId);
}

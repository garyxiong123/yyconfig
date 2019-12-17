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
package com.yofish.gary.biz.service;

import com.yofish.gary.api.dto.req.*;
import com.yofish.gary.api.dto.rsp.UserDetailRspDTO;
import com.yofish.gary.api.dto.rsp.UserLoginRspDTO;
import com.yofish.gary.api.dto.rsp.UserMenuPermissionRspDTO;
import com.yofish.gary.api.dto.rsp.UserQueryRspDTO;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.tuple.Tuple2;
import com.youyu.common.api.PageData;

import java.util.List;
import java.util.Set;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户service
 */
public interface UserService{

    /**
     * 登录
     *
     * @param userLoginReqDTO
     * @return
     */
    UserLoginRspDTO login(UserLoginReqDTO userLoginReqDTO);

    /**
     * 登出
     */
    void logout();

    /**
     * 根据用户名和密码认证用户
     *
     * @param username
     * @param password
     * @return
     */
    User getAuthenticationUser(String username, String password);

    /**
     * 修改用户密码
     *
     * @param userModifyPasswordReqDTO
     */
    void modifyPassword(UserModifyPasswordReqDTO userModifyPasswordReqDTO);

    /**
     * 添加用户
     *
     * @param userAddReqDTO
     * @return
     */
    Long add(UserAddReqDTO userAddReqDTO);

    /**
     * 删除用户
     *
     * @param userDeleteReqDTO
     */
    void delete(UserDeleteReqDTO userDeleteReqDTO);

    /**
     * 编辑用户
     * 注:暂不做角色校验,只做权限控制按钮
     *
     * @param userEditReqDTO
     */
    void edit(UserEditReqDTO userEditReqDTO);

    /**
     * 查询分页用户
     *
     * @param userQueryReqDTO
     * @return
     */
    PageData<UserQueryRspDTO> getPage(UserQueryReqDTO userQueryReqDTO);

    /**
     * 查询用户列表
     *
     * @param userQueryReqDTO
     * @return
     */
    List<UserQueryRspDTO> getList(UserQueryReqDTO userQueryReqDTO);


    /**
     * 默认未授权访问处理
     */
    void unauthorized();

    /**
     * 默认未登录访问处理
     */
    void needLogin();

    /**
     * 根据用户获取角色和权限二元组
     *
     * @param userId
     * @return
     */
    Tuple2<Set<String>, Set<String>> getRolePermissionTuple2(Long userId);

    /**
     * 根据用户id查询url地址是否存在
     *
     * @param userId
     * @param url
     * @return
     */
    boolean hasUrlPermission(Long userId, String url);

    /**
     * 获取用户菜单权限列表
     *
     * @param userMenuPermissionReqDTO
     * @return
     */
    List<UserMenuPermissionRspDTO> getUserMenuPermissions(UserMenuPermissionReqDTO userMenuPermissionReqDTO);

    /**
     * 根据用户id查询用户详细信息
     *
     * @param userId
     * @return
     */
    UserDetailRspDTO getUserDetail(Long userId);

    /**
     * 根据用户id判断用户是否是管理员
     *
     * @param userId
     * @return
     */
    Boolean isAdmin(Long userId);
}

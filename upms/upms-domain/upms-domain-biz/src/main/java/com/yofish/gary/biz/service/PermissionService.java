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
import com.yofish.gary.api.dto.rsp.PermissionQueryRspDTO;
import com.yofish.gary.api.dto.rsp.PermissionTreeRspDTO;
import com.youyu.common.api.PageData;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限service
 */
public interface PermissionService {

    /**
     * 添加权限
     *
     * @param permissionAddReqDTO
     */
    void add(PermissionAddReqDTO permissionAddReqDTO);

    /**
     * 删除权限
     *
     * @param permissionDeleteReqDTO
     */
    void delete(PermissionDeleteReqDTO permissionDeleteReqDTO);

    /**
     * 编辑权限
     *
     * @param permissionEditReqDTO
     */
    void edit(PermissionEditReqDTO permissionEditReqDTO);

    /**
     * 查询权限
     *
     * @param permissionQueryReqDTO
     * @return
     */
    PageData<PermissionQueryRspDTO> getPage(PermissionQueryReqDTO permissionQueryReqDTO);

    /**
     * 判断requestUrl地址是否存在
     * 注:存在即需要验证
     *
     * @param url
     * @return
     */
    boolean isUrlExist(String url);

    /**
     * 查询权限树
     *
     * @param permissionTreeReqDTO
     * @return
     */
    List<PermissionTreeRspDTO> getPermissionTree(PermissionTreeReqDTO permissionTreeReqDTO);

}

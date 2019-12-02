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

import com.yofish.gary.api.dto.req.RoleAddReqDTO;
import com.yofish.gary.api.dto.req.RoleDeleteReqDTO;
import com.yofish.gary.api.dto.req.RoleEditReqDTO;
import com.yofish.gary.api.dto.req.RoleQueryReqDTO;
import com.yofish.gary.api.dto.rsp.RoleQueryRspDTO;
import com.yofish.gary.biz.domain.Role;
import com.youyu.common.api.PageData;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 角色service
 */
public interface RoleService {

    /**
     * 添加角色
     *
     * @param roleAddReqDTO
     */
    void add(RoleAddReqDTO roleAddReqDTO);

    /**
     * 删除角色
     *
     * @param roleDeleteReqDTO
     */
    void delete(RoleDeleteReqDTO roleDeleteReqDTO);

    /**
     * 编辑角色
     *
     * @param roleEditReqDTO
     */
    void edit(RoleEditReqDTO roleEditReqDTO);

    /**
     * 查询角色
     *
     * @param roleQueryReqDTO
     * @return
     */
    PageData<RoleQueryRspDTO> getPage(RoleQueryReqDTO roleQueryReqDTO);

    Role findByRoleName(String roleName);
}

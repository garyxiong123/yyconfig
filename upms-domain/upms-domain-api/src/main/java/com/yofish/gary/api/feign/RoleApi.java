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

import com.yofish.gary.api.dto.req.RoleDeleteReqDTO;
import com.yofish.gary.api.dto.req.RoleEditReqDTO;
import com.yofish.gary.api.dto.req.RoleQueryReqDTO;
import com.yofish.gary.api.dto.rsp.RoleQueryRspDTO;
import com.yofish.gary.api.dto.req.RoleAddReqDTO;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 角色Api
 */
@RequestMapping("/role")
public interface RoleApi {

    /**
     * 添加角色
     *
     * @param roleAddReqDTO
     * @return
     */
    @ApiOperation("添加角色")
    @PostMapping("/add")
    Result add(@Valid @RequestBody RoleAddReqDTO roleAddReqDTO);

    /**
     * 删除角色
     *
     * @param roleDeleteReqDTO
     * @return
     */
    @ApiOperation("删除角色")
    @PostMapping("/delete")
    Result delete(@Valid @RequestBody RoleDeleteReqDTO roleDeleteReqDTO);

    /**
     * 编辑角色
     *
     * @param roleEditReqDTO
     * @return
     */
    @ApiOperation("编辑角色")
    @PostMapping("/edit")
    Result edit(@Valid @RequestBody RoleEditReqDTO roleEditReqDTO);

    /**
     * 查询角色
     *
     * @param roleQueryReqDTO
     * @return
     */
    @ApiOperation("查询角色")
    @PostMapping("/getPage")
    Result<PageData<RoleQueryRspDTO>> getPage(@RequestBody RoleQueryReqDTO roleQueryReqDTO);
}

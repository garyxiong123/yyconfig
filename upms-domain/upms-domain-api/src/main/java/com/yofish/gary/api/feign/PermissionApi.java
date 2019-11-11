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
import com.yofish.gary.api.dto.rsp.PermissionQueryRspDTO;
import com.yofish.gary.api.dto.rsp.PermissionTreeRspDTO;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限Api
 */
@RequestMapping("/permission")
public interface PermissionApi {

    /**
     * 添加权限
     *
     * @param permissionAddReqDTO
     * @return
     */
    @ApiOperation("添加权限")
    @PostMapping("/add")
    Result add(@Valid @RequestBody PermissionAddReqDTO permissionAddReqDTO);

    /**
     * 删除权限
     *
     * @param permissionDeleteReqDTO
     * @return
     */
    @ApiOperation("删除权限")
    @PostMapping("/delete")
    Result delete(@Valid @RequestBody PermissionDeleteReqDTO permissionDeleteReqDTO);

    /**
     * 编辑权限
     *
     * @param permissionEditReqDTO
     * @return
     */
    @ApiOperation("编辑权限")
    @PostMapping("/edit")
    Result edit(@Valid @RequestBody PermissionEditReqDTO permissionEditReqDTO);

    /**
     * 查询权限
     *
     * @param permissionQueryReqDTO
     * @return
     */
    @ApiOperation("查询权限")
    @PostMapping("/getPage")
    Result<PageData<PermissionQueryRspDTO>> getPage(@RequestBody PermissionQueryReqDTO permissionQueryReqDTO);

    /**
     * 查询权限树
     *
     * @param permissionTreeReqDTO
     * @return
     */
    @ApiOperation("查询权限树")
    @PostMapping("/getPermissionTree")
    Result<List<PermissionTreeRspDTO>> getPermissionTree(@RequestBody PermissionTreeReqDTO permissionTreeReqDTO);
}

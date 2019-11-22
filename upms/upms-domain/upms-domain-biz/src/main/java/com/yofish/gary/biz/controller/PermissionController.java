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
package com.yofish.gary.biz.controller;

import com.yofish.gary.api.dto.req.*;
import com.yofish.gary.api.dto.rsp.PermissionQueryRspDTO;
import com.yofish.gary.api.dto.rsp.PermissionTreeRspDTO;
import com.yofish.gary.api.feign.PermissionApi;
import com.yofish.gary.biz.service.PermissionService;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.youyu.common.api.Result.ok;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限controller
 */
@RestController
@RequestMapping(value = "/permission")
public class PermissionController implements PermissionApi {

    @Autowired
    private PermissionService permissionService;

    @Override
    @PostMapping("/add")
    public Result add(@Valid @RequestBody PermissionAddReqDTO permissionAddReqDTO) {
        permissionService.add(permissionAddReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/delete")
    public Result delete(@Valid @RequestBody PermissionDeleteReqDTO permissionDeleteReqDTO) {
        permissionService.delete(permissionDeleteReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/edit")
    public Result edit(@Valid @RequestBody PermissionEditReqDTO permissionEditReqDTO) {
        permissionService.edit(permissionEditReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/getPage")
    public Result<PageData<PermissionQueryRspDTO>> getPage(@RequestBody PermissionQueryReqDTO permissionQueryReqDTO) {
        return ok(permissionService.getPage(permissionQueryReqDTO));
    }

    @Override
    @PostMapping("/getPermissionTree")
    public Result<List<PermissionTreeRspDTO>> getPermissionTree(@RequestBody PermissionTreeReqDTO permissionTreeReqDTO) {
        return ok(permissionService.getPermissionTree(permissionTreeReqDTO));
    }
}

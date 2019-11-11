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

import com.yofish.gary.api.dto.req.RoleAddReqDTO;
import com.yofish.gary.api.dto.req.RoleDeleteReqDTO;
import com.yofish.gary.api.dto.req.RoleEditReqDTO;
import com.yofish.gary.api.dto.req.RoleQueryReqDTO;
import com.yofish.gary.api.dto.rsp.RoleQueryRspDTO;
import com.yofish.gary.api.feign.RoleApi;
import com.yofish.gary.biz.service.RoleService;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.youyu.common.api.Result.ok;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户controller
 */
@RestController
@RequestMapping(value = "/role")
public class RoleController implements RoleApi {

    @Autowired
    private RoleService roleService;

    @Override
    @PostMapping("/add")
    public Result add(@Valid @RequestBody RoleAddReqDTO roleAddReqDTO) {
        roleService.add(roleAddReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/delete")
    public Result delete(@Valid @RequestBody RoleDeleteReqDTO roleDeleteReqDTO) {
        roleService.delete(roleDeleteReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/edit")
    public Result edit(@Valid @RequestBody RoleEditReqDTO roleEditReqDTO) {
        roleService.edit(roleEditReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/getPage")
    public Result<PageData<RoleQueryRspDTO>> getPage(@RequestBody RoleQueryReqDTO roleQueryReqDTO) {
        return ok(roleService.getPage(roleQueryReqDTO));
    }
}

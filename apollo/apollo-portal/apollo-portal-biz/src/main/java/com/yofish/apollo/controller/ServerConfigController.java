/*
 *    Copyright 2019-2020 the original author or authors.
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
package com.yofish.apollo.controller;


import com.yofish.apollo.component.AppPreAuthorize;
import com.yofish.apollo.component.config.ServerConfigKey;
import com.yofish.apollo.domain.ServerConfig;
import com.yofish.apollo.model.ServerConfigModel;
import com.yofish.apollo.repository.ServerConfigRepository;
import com.youyu.common.api.Result;
import com.yofish.yyconfig.common.common.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * 配置中心本身需要一些配置,这些配置放在数据库里面
 * 系统启动时会根据配置进行相关参数初始化，这里可以查看和修改
 *
 * @see ServerConfigKey
 * @author SongJunWang
 */
@Api(description = "系统配置")
@RestController
@RequestMapping("/server/config")
public class ServerConfigController {

    @Autowired
    private ServerConfigRepository serverConfigRepository;

    @AppPreAuthorize(AppPreAuthorize.Authorize.SuperAdmin)
    @PostMapping
    @ApiOperation("创建或修改")
    public Result<ServerConfig> createOrUpdate(@RequestBody @Valid ServerConfigModel model) {

        ServerConfig storedConfig = serverConfigRepository.findByKey(model.getKey());

        if (isNull(storedConfig)) {
            return Result.ok(serverConfigRepository.save(ServerConfig.builder().key(model.getKey()).value(model.getValue()).comment(model.getComment()).build()));
        } else {//update
            BeanUtils.copyEntityProperties(model, storedConfig);
            return Result.ok(serverConfigRepository.save(storedConfig));
        }
    }

    @AppPreAuthorize(AppPreAuthorize.Authorize.SuperAdmin)
    @GetMapping("{key:\\w+}")
    @ApiOperation("查询单个配置项")
    public Result<ServerConfig> loadServerConfig(@PathVariable ServerConfigKey key) {
        return Result.ok(serverConfigRepository.findByKey(key));
    }

    @GetMapping("list")
    @ApiOperation("查询系统配置列表")
    @AppPreAuthorize(AppPreAuthorize.Authorize.SuperAdmin)
    public Result<List<ServerConfig>> getList() {
        return Result.ok(serverConfigRepository.findAll());
    }

}

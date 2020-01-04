package com.yofish.apollo.controller;


import com.yofish.apollo.component.AppPreAuthorize;
import com.yofish.apollo.config.ServerConfigKey;
import com.yofish.apollo.domain.ServerConfig;
import com.yofish.apollo.repository.ServerConfigRepository;
import com.youyu.common.api.Result;
import common.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result<ServerConfig> createOrUpdate(ServerConfig serverConfig) {

        ServerConfig storedConfig = serverConfigRepository.findByKey(serverConfig.getKey());

        if (isNull(storedConfig)) {
            return Result.ok(serverConfigRepository.save(serverConfig));
        } else {//update
            BeanUtils.copyEntityProperties(serverConfig, storedConfig);
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

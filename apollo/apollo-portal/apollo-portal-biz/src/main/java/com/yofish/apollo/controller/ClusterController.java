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

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.service.AppEnvClusterService;
import com.youyu.common.api.Result;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import common.utils.InputValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Api(description = "项目的集群")
@RestController
public class ClusterController {

    @Autowired
    private AppEnvClusterService appEnvClusterService;

    @ApiOperation("创建集群")
    @PostMapping("/apps/{appId:\\d+}/envs/{envs}/clusters/{clusterName}")
    public Result<List<AppEnvCluster>> createCluster(@PathVariable Long appId, @PathVariable String envs, @PathVariable String clusterName) {

        if (!InputValidator.isValidClusterNamespace(clusterName)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("Cluster格式错误: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
        }

        List<AppEnvCluster> appEnvClusterList = new ArrayList<>();
        Arrays.stream(envs.split(",")).forEach(env -> {
            AppEnvCluster appEnvCluster = AppEnvCluster.builder().app(new App(appId)).env(env).name(clusterName).build();
            this.appEnvClusterService.createAppEnvCluster(appEnvCluster);
            appEnvClusterList.add(appEnvCluster);
        });

        return Result.ok(appEnvClusterList);
    }

    @ApiOperation("删除集群")
    @DeleteMapping("/apps/{appId:\\d+}/envs/{env}/clusters/{clusterName:.+}")
    public Result deleteCluster(@PathVariable Long appId, @PathVariable String env, @PathVariable String clusterName) {
        this.appEnvClusterService.deleteAppEnvCluster(AppEnvCluster.builder().app(new App(appId)).env(env).name(clusterName).build());
        return Result.ok();
    }

    @ApiOperation("查询集群信息")
    @GetMapping("/apps/{appId:\\d+}/envs/{env}/clusters/{clusterName:.+}")
    public AppEnvCluster loadCluster(@PathVariable("appId") Long appId, @PathVariable String env, @PathVariable("clusterName") String clusterName) {
        AppEnvCluster appEnvCluster = this.appEnvClusterService.getAppEnvCluster(appId, env, clusterName);
        return appEnvCluster;
    }

}

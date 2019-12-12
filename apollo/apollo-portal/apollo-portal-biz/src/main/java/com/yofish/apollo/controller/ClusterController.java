package com.yofish.apollo.controller;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.repository.ClusterRepository;
import com.youyu.common.api.Result;
import common.exception.BadRequestException;
import common.utils.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class ClusterController {

    @Autowired
    private ClusterRepository clusterRepository;

    @PostMapping(value = "apps/{appId}/envs/{env:\\d+}/clusters/{clusterName}")
    public Result<AppEnvCluster> createCluster(@PathVariable Long appId, @PathVariable String env, @PathVariable String clusterName) {

        if (!InputValidator.isValidClusterNamespace(clusterName)) {
            throw new BadRequestException(String.format("Cluster格式错误: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
        }

        AppEnvCluster appEnvCluster = AppEnvCluster.builder().app(new App(appId)).env(env).name(clusterName).build();
        this.clusterRepository.save(appEnvCluster);
        return Result.ok(appEnvCluster);
    }

    @DeleteMapping(value = "apps/{appId:\\d+}/envs/{env}/clusters/{clusterName:.+}")
    public Result deleteCluster(@PathVariable Long appId, @PathVariable String env, @PathVariable String clusterName) {
        this.clusterRepository.delete(AppEnvCluster.builder().app(new App(appId)).env(env).name(clusterName).build());
        return Result.ok();
    }

    @GetMapping(value = "apps/{appId:\\d+}/envs/{env}/clusters/{clusterName:.+}")
    public AppEnvCluster loadCluster(@PathVariable("appId") Long appId, @PathVariable String env, @PathVariable("clusterName") String clusterName) {
        AppEnvCluster appEnvCluster = this.clusterRepository.findClusterByAppAndEnvAndName(new App(appId), env, clusterName);
        return appEnvCluster;
    }

}

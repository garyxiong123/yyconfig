package com.yofish.apollo.controller;

import com.yofish.apollo.domain.Cluster;
import com.yofish.apollo.spi.UserInfoHolder;
import com.youyu.common.api.Result;
import common.exception.BadRequestException;
import common.utils.InputValidator;
import common.utils.RequestPrecondition;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
public class ClusterController {

//    @Autowired
//    private ClusterService clusterService;

    //  @PreAuthorize(value = "@permissionValidator.hasCreateClusterPermission(#appId)")
    @RequestMapping(value = "apps/{appId}/envs/{env}/clusters", method = RequestMethod.POST)
    public Result<Cluster> createCluster(@PathVariable String appId, @PathVariable String env,
                                        @RequestBody Cluster cluster) {

//    checkModel(Objects.nonNull(cluster));
//        RequestPrecondition.checkArgumentsNotEmpty(cluster.getAppId(), cluster.getName());
//
//        if (!InputValidator.isValidClusterNamespace(cluster.getName())) {
//            throw new BadRequestException(String.format("Cluster格式错误: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
//        }
//
//
//        return clusterService.createCluster(Env.valueOf(env), cluster);
        return null;
    }

    //  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
    @RequestMapping(value = "apps/{appId}/envs/{env}/clusters/{clusterName:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteCluster(@PathVariable String appId, @PathVariable String env,
                                              @PathVariable String clusterName) {
//    clusterService.deleteCluster(Env.fromString(env), appId, clusterName);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "apps/{appId}/envs/{env}/clusters/{clusterName:.+}", method = RequestMethod.GET)
    public Cluster loadCluster(@PathVariable("appId") String appId, @PathVariable String env, @PathVariable("clusterName") String clusterName) {

//    return clusterService.loadCluster(appId, Env.fromString(env), clusterName);
        return null;
    }

}

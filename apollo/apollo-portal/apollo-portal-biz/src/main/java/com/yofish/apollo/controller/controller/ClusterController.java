package com.yofish.apollo.controller.controller;

import com.yofish.apollo.spi.UserInfoHolder;
import common.exception.BadRequestException;
import common.utils.InputValidator;
import common.utils.RequestPrecondition;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static common.utils.RequestPrecondition.checkModel;


@RestController
public class ClusterController {

  /*@Autowired
  private ClusterService clusterService;
  @Autowired
  private UserInfoHolder userInfoHolder;

  @PreAuthorize(value = "@permissionValidator.hasCreateClusterPermission(#appId)")
  @RequestMapping(value = "apps/{appId}/envs/{env}/clusters", method = RequestMethod.POST)
  public ClusterEntity createCluster(@PathVariable String appId, @PathVariable String env,
                                     @RequestBody ClusterEntity cluster) {

    checkModel(Objects.nonNull(cluster));
    RequestPrecondition.checkArgumentsNotEmpty(cluster.getAppId(), cluster.getName());

    if (!InputValidator.isValidClusterNamespace(cluster.getName())) {
      throw new BadRequestException(String.format("Cluster格式错误: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
    }

    String operator = userInfoHolder.getUser().getUserId();
    cluster.setDataChangeLastModifiedBy(operator);
    cluster.setDataChangeCreatedBy(operator);

    return clusterService.createCluster(Env.valueOf(env), cluster);
  }

  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @RequestMapping(value = "apps/{appId}/envs/{env}/clusters/{clusterName:.+}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteCluster(@PathVariable String appId, @PathVariable String env,
                                            @PathVariable String clusterName){
    clusterService.deleteCluster(Env.fromString(env), appId, clusterName);
    return ResponseEntity.ok().build();
  }

  @RequestMapping(value = "apps/{appId}/envs/{env}/clusters/{clusterName:.+}", method = RequestMethod.GET)
  public ClusterEntity loadCluster(@PathVariable("appId") String appId, @PathVariable String env, @PathVariable("clusterName") String clusterName) {

    return clusterService.loadCluster(appId, Env.fromString(env), clusterName);
  }*/

}

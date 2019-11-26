package com.yofish.apollo.controller.controller;

import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.common.utils.InputValidator;
import com.ctrip.framework.apollo.common.utils.RequestPrecondition;
import com.ctrip.framework.apollo.config.UserInfoHolder;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.model.entity.ClusterEntity;
import com.ctrip.framework.apollo.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.ctrip.framework.apollo.common.utils.RequestPrecondition.checkModel;

@RestController
public class ClusterController {

  @Autowired
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
  }

}

package com.yofish.apollo.controller.controller;

import com.ctrip.framework.apollo.common.utils.RequestPrecondition;
import com.ctrip.framework.apollo.component.PermissionValidator;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.model.entity.Commit;
import com.ctrip.framework.apollo.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
public class CommitController {

  @Autowired
  private CommitService commitService;

  @Autowired
  private PermissionValidator permissionValidator;

  @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/commits", method = RequestMethod.GET)
  public List<Commit> find(@PathVariable String appId, @PathVariable String env,
                           @PathVariable String clusterName, @PathVariable String namespaceName,
                           @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
    if (permissionValidator.shouldHideConfigToCurrentUser(appId, env, namespaceName)) {
      return Collections.emptyList();
    }

    RequestPrecondition.checkNumberPositive(size);
    RequestPrecondition.checkNumberNotNegative(page);

    return commitService.find(appId, Env.valueOf(env), clusterName, namespaceName, page, size);

  }

}

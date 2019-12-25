package com.yofish.apollo.controller;

import com.yofish.apollo.domain.Commit;
import com.yofish.apollo.service.CommitService;
import com.youyu.common.api.Result;
import common.utils.RequestPrecondition;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.Iterator;
import java.util.List;


@RestController
@RequestMapping("commit")
public class CommitController {

  @Autowired
  private CommitService commitService;


    @PostMapping(value = "find")
    public Result<List<Commit>> find(@RequestParam("appEnvClusterNamespaceId") Long appEnvClusterNamespaceId) {
       List<Commit> commits= commitService.find(appEnvClusterNamespaceId);
       return Result.ok(commits);

    }

}

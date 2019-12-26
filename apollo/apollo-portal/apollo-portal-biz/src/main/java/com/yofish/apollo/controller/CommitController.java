package com.yofish.apollo.controller;

import com.yofish.apollo.domain.Commit;
import com.yofish.apollo.dto.CommitDto;
import com.yofish.apollo.service.CommitService;
import com.youyu.common.api.Result;
import common.utils.BeanUtils;
import common.utils.RequestPrecondition;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


@RestController
@RequestMapping("commit")
public class CommitController {

  @Autowired
  private CommitService commitService;


    @GetMapping(value = "find")
    public Result<List<CommitDto>> find(@RequestParam("appEnvClusterNamespaceId") Long appEnvClusterNamespaceId) {
       List<Commit> commits= commitService.find(appEnvClusterNamespaceId);
       List<CommitDto> commitDtos=new ArrayList<>();
       for (Commit commit:commits){
           CommitDto commitDto=new CommitDto();
           BeanUtils.copyEntityProperties(commit,commitDto);
           commitDto.setAppEnvClusterNamespace(commit.getAppEnvClusterNamespace().getId());
           commitDtos.add(commitDto);
       }
       return Result.ok(commitDtos);

    }

}

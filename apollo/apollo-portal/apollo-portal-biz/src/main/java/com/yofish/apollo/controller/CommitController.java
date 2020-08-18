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

import com.yofish.apollo.domain.Commit;
import com.yofish.apollo.api.dto.CommitDto;
import com.yofish.apollo.service.CommitService;
import com.youyu.common.api.Result;
import common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
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

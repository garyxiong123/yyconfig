package com.yofish.apollo.controller;

import com.google.common.base.Splitter;
import com.yofish.apollo.dto.InstanceDTO;
import com.yofish.apollo.dto.InstanceNamespaceReq;
import com.yofish.apollo.service.InstanceService;
import com.yofish.apollo.util.PageQuery;
import com.youyu.common.api.Result;

import com.youyu.common.exception.BizException;
import common.dto.PageDTO;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("instances")
public class InstanceController {
    @Autowired
    private InstanceService instanceService;

    private static final Splitter RELEASES_SPLITTER = Splitter.on(",").omitEmptyStrings()
            .trimResults();

//    @Autowired
//    private InstanceService instanceService;
//    @Autowired
//    private ReleaseService releaeService;

    @RequestMapping(value = "by-release", method = RequestMethod.GET)
    public Result<PageDTO<InstanceDTO>> getByRelease(@RequestBody PageQuery<Long> releasePage) {
        Pageable pageable = PageRequest.of(releasePage.getPageNo(), releasePage.getPageSize());
        PageDTO<InstanceDTO> pa=  instanceService.getByRelease(releasePage.getData(),pageable);
        return Result.ok(pa);
    }

/*
    @RequestMapping(value = "by-namespace", method = RequestMethod.GET)
    public Page<InstanceDTO> getByNamespace(PageQuery<InstanceNamespaceReq> instanceNamespaceReqPageQuery) {
        Pageable pageable = PageRequest.of(instanceNamespaceReqPageQuery.getPageNo(), instanceNamespaceReqPageQuery.getPageSize());
      return instanceService.findInstancesByNamespace(instanceNamespaceReqPageQuery.getData().getReleaseId(),pageable);

    }*/

    @RequestMapping(value = "/envs/{env}/instances/by-namespace/count", method = RequestMethod.GET)
    public ResponseEntity<Number> getInstanceCountByNamespace(@PathVariable String env, @RequestParam String appId,
                                                              @RequestParam String clusterName,
                                                              @RequestParam String namespaceName) {

//        int count = instanceService.getInstanceCountByNamepsace(appId, Env.valueOf(env), clusterName, namespaceName);
//        return ResponseEntity.ok(new Number(count));
        return null;
    }

    @RequestMapping(value = "/by-namespace-and-releases-not-in", method = RequestMethod.GET)
    public List<InstanceDTO> getByReleasesNotIn(@RequestParam Long appEnvClusterNamespaceId,
                                             @RequestParam String releaseIds) {

        Set<Long> releaseIdSet = RELEASES_SPLITTER.splitToList(releaseIds).stream().map(Long::parseLong)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(releaseIdSet)) {
            throw new BizException("release ids can not be empty");
        }

        return instanceService.getByReleasesNotIn(appEnvClusterNamespaceId, releaseIdSet);
    }


}

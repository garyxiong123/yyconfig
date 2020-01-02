package com.yofish.apollo.controller;

import com.google.common.base.Splitter;
import com.yofish.apollo.domain.Instance;
import com.yofish.apollo.dto.InstanceDTO;
import com.yofish.apollo.dto.InstanceNamespaceReq;
import com.yofish.apollo.service.InstanceService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.apollo.util.PageQuery;
import com.youyu.common.api.Result;

import com.youyu.common.exception.BizException;
import common.dto.PageDTO;
import framework.apollo.core.enums.Env;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
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

    private static final Splitter RELEASES_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    @Autowired
    private ReleaseService releaeService;


    @ApiOperation("使用最新配置")
    @RequestMapping(value = "by-release", method = RequestMethod.GET)
    public Result<PageDTO<InstanceDTO>> getByRelease(@RequestBody PageQuery<Long> releasePage) {
        Pageable pageable = PageRequest.of(releasePage.getPageNo(), releasePage.getPageSize());
        //最新的releaseId
        Long releaseId4Lastest = releasePage.getData();
        PageDTO<InstanceDTO> pa = instanceService.getByRelease(releaseId4Lastest, pageable);
        return Result.ok(pa);
    }


    @ApiOperation("所有实例")
    @RequestMapping(value = "by-namespace", method = RequestMethod.GET)
    public Page<InstanceDTO> getByNamespace(PageQuery<InstanceNamespaceReq> instancePageQuery) {
        Pageable pageable = PageRequest.of(instancePageQuery.getPageNo(), instancePageQuery.getPageSize());
        return instanceService.findInstancesByNamespace(instancePageQuery.getData().getNamespaceId(), pageable);

    }

    @ApiOperation("所有实例数")
    @RequestMapping(value = "/namespaceId/{namespaceId}/count", method = RequestMethod.GET)
    public Result<Number> getInstanceCountByNamespace(@PathVariable Long namespaceId) {

        int count = instanceService.getInstanceCountByNamepsace(namespaceId);
        return Result.ok(count);
    }

    @ApiOperation("使用的非最新配置的实例")
    @RequestMapping(value = "/namespaceId/{namespaceId}/releaseIds/{releaseIds}/by-namespace-and-releases-not-in", method = RequestMethod.GET)
    public List<InstanceDTO> getByReleasesNotIn(@RequestParam Long namespaceId, @RequestParam String releaseIds) {

        Set<Long> releaseIdSet = RELEASES_SPLITTER.splitToList(releaseIds).stream().map(Long::parseLong).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(releaseIdSet)) {
            throw new BizException("release ids can not be empty");
        }

        return instanceService.getByReleasesNotIn(namespaceId, releaseIdSet);
    }


}

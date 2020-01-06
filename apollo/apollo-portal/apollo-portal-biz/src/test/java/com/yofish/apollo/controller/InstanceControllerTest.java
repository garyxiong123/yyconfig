package com.yofish.apollo.controller;

import com.yofish.apollo.dto.InstanceDTO;
import com.yofish.apollo.dto.InstanceNamespaceReq;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.util.PageQuery;
import com.youyu.common.api.Result;
import common.dto.PageDTO;
import controller.AbstractControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @Author: xiongchengwei
 * @Date: 2020/1/2 下午1:40
 */
public class InstanceControllerTest extends AbstractControllerTest {

    @Autowired
    private InstanceController instanceController;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getByRelease() {

        PageQuery<Long> longPageQuery = new PageQuery();
        Result<PageDTO<InstanceDTO>> byRelease = instanceController.getByRelease(longPageQuery);

    }

    @Test
    public void getByNamespace() {
        Long namespaceId = namespaceRepository.findAll().get(0).getId();

        PageQuery<InstanceNamespaceReq> instanceNamespaceReqPageQuery = createPageQuery(namespaceId);
        instanceController.getByNamespace(instanceNamespaceReqPageQuery);
    }

    private PageQuery<InstanceNamespaceReq> createPageQuery(Long namespaceId) {
        PageQuery<InstanceNamespaceReq> instanceNamespaceReqPageQuery = new PageQuery<>();
        InstanceNamespaceReq namespaceReq = new InstanceNamespaceReq();
        namespaceReq.setNamespaceId(namespaceId);
        instanceNamespaceReqPageQuery.setData(namespaceReq);
        return instanceNamespaceReqPageQuery;
    }

    @Test
    public void getInstanceCountByNamespace() {
        Long namespaceId = namespaceRepository.findAll().get(0).getId();
        instanceController.getInstanceCountByNamespace(namespaceId);
    }

    @Test
    public void getByReleasesNotIn() {
    }
}
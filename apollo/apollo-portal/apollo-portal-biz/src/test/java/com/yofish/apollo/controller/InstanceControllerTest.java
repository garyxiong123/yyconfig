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

import com.yofish.apollo.api.dto.InstanceDTO;
import com.yofish.apollo.api.dto.InstanceNamespaceReq;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.component.util.PageQuery;
import com.youyu.common.api.Result;
import com.yofish.yyconfig.common.common.dto.PageDTO;
import controller.AbstractControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
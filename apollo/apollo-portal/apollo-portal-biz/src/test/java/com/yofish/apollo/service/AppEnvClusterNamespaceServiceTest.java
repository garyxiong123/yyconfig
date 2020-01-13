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
package com.yofish.apollo.service;

import com.yofish.apollo.model.bo.NamespaceVO;
import common.dto.NamespaceDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yofish.apollo.JpaApplication.class})
public class AppEnvClusterNamespaceServiceTest {

    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;

    @Test
    public void createNamespace() {
        String env = "dev";
        /*NamespaceDTO dto = new NamespaceDTO();
        dto.setAppId(1L);
        dto.setClusterName("default");
        dto.setNamespaceName("other-config");
        NamespaceDTO appNamespace = this.appEnvClusterNamespaceService.createNamespace(env, dto);
        Assert.assertEquals(dto.getNamespaceName(), appNamespace.getNamespaceName());*/
    }

    @Test
    public void findNamespaceVOsTest() {
        String appCode = "apollo-mini";
        String env = "dev";
        String cluster = "default";
        List<NamespaceVO> namespaceVOs = this.appEnvClusterNamespaceService.findNamespaceVOs(appCode, env, cluster);

    }
}
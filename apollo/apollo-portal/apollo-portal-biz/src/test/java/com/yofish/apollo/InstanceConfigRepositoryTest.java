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
package com.yofish.apollo;

import com.yofish.apollo.domain.*;
import com.yofish.apollo.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 下午2:44
 */
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PortalApplication.class})
public class InstanceConfigRepositoryTest {
    @Autowired
    private InstanceConfigRepository instanceConfigRepository;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    @Autowired
    private InstanceRepository instanceRepository;

    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test(){

        AppEnvClusterNamespace namespace = namespaceRepository.findAll().get(0);
        Instance instance = createInstance();
        InstanceConfig instanceConfig = createInstanceConfig(namespace, instance);
        instanceConfigRepository.save(instanceConfig);
        Page<InstanceConfig> instanceConfigs = instanceConfigRepository.findByNamespaceId(namespace.getId(), Pageable.unpaged());
        System.out.println(instanceConfigs);
    }

    private Instance createInstance() {
        Instance instance = new Instance();
        instance.setIp("10.0.11.18");
        instance.setDataCenter("shanghai");
        instance.setAppEnvCluster(null);
        instanceRepository.save(instance);
        return instance;
    }

    private InstanceConfig createInstanceConfig(AppEnvClusterNamespace namespace, Instance instance) {
        InstanceConfig instanceConfig = new InstanceConfig();
        instanceConfig.setNamespace(namespace);
        instanceConfig.setInstance(instance);
        instanceConfigRepository.save(instanceConfig);
        return instanceConfig;
    }

}
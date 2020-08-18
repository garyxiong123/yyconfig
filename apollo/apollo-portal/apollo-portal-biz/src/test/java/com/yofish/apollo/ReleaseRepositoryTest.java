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

import com.google.common.collect.Sets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.repository.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 下午2:44
 */
@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PortalApplication.class})
public class ReleaseRepositoryTest {
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    @Autowired
    private ReleaseRepository releaseRepository;

    private AppEnvClusterNamespace namespace;

    private String releaseKey = "123";


    @Before
    public void setUp() throws Exception {
        namespace = namespaceRepository.findAll().get(0);

        Map<String, String> configMap = new HashMap<>();
//        configMap.put("name","22");

        Release4Main release4Main = Release4Main.builder().namespace(namespace).isEmergencyPublish(false).configurations(configMap).comment("comment").name("123").build();

        releaseRepository.save(release4Main);
    }


    @Test
    public void testFindLastestRelease() {

        Release release = releaseRepository.findFirstByAppEnvClusterNamespace_IdAndAbandonedIsFalseOrderByIdDesc(namespace.getId());
        Assert.assertNotNull(release);
    }


    @Test
    public void testFindByReleaseKeys() {
        Set<String> releaseKeys = Sets.newHashSet(releaseKey);

        List<Release> releases = releaseRepository.findReleasesByReleaseKeyIn(releaseKeys);
        Assert.assertNotNull(releases);
    }


    @Test
    public void testFindById() {
        Long id = releaseRepository.findAll().get(0).getId();
        Release releases = releaseRepository.findByIdAndAbandonedFalse(id);
        Assert.assertNotNull(releases);
    }


}
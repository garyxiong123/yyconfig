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
@SpringBootTest(classes = {JpaApplication.class})
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
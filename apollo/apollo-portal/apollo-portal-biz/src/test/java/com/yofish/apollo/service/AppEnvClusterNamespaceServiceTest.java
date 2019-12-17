package com.yofish.apollo.service;

import common.dto.NamespaceDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
}
package com.yofish.apollo.service;

import com.yofish.apollo.domain.AppNamespace4Protect;
import com.yofish.apollo.domain.AppNamespace4Public;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2020-01-08
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yofish.apollo.JpaApplication.class})
public class AppNamespaceServiceTest {
    @Autowired
    private AppNamespaceService appNamespaceService;

    @Test
    public void findAllPublicAppNamespace() {
        List<AppNamespace4Public> allPublicAppNamespace = appNamespaceService.findAllPublicAppNamespace();
        Assert.assertTrue(!ObjectUtils.isEmpty(allPublicAppNamespace));
    }

    @Test
    public void findAllProtectAppNamespaceByAuthorized() {
        String appCode = "apollo-mini";
        List<AppNamespace4Protect> allProtectAppNamespaceByAuthorized = appNamespaceService.findAllProtectAppNamespaceByAuthorized(appCode);

        Assert.assertTrue(!ObjectUtils.isEmpty(allProtectAppNamespaceByAuthorized));
    }
}
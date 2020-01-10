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
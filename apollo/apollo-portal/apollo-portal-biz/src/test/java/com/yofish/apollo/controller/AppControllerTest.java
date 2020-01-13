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

import com.yofish.apollo.domain.App;
import com.yofish.apollo.model.model.AppModel;
import com.yofish.apollo.model.vo.EnvClusterInfo;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.UserRepository;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import controller.AbstractControllerTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2020-01-07
 */
public class AppControllerTest extends AbstractControllerTest {
    @Autowired
    private AppController appController;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * 创建项目
     */
    @Test
    public void create() {
        User user = userRepository.findAll().get(0);

        AppModel appModel = new AppModel("阿波罗测试项目", "apollo-test", user.getDepartment().getId(), user.getId(), null);
        Result<App> appResult = appController.create(appModel);

        Assert.assertTrue(appResult.success());
    }

    /**
     * 分页条件查询
     */
    @Test
    public void searchByAppCodeOrAppName() {
        Result<PageData<App>> pageDataResult = appController.searchByAppCodeOrAppName("apollo", 1, 10);

        Assert.assertTrue(pageDataResult.success());
    }


    @Test
    public void update() {
        App app = appRepository.findAll().get(0);
        AppModel appModel = new AppModel("配置中心演示项目", "apollo-test", app.getDepartment().getId(), app.getAppOwner().getId(), null);

        Result<App> updateResult = appController.update(app.getId(), appModel);
        Assert.assertTrue(updateResult.success());
    }

    @Test
    public void getAppByCode() {
        Result<App> appByCode = appController.getAppByCode("apollo-test");

        Assert.assertTrue(appByCode.success());
    }

    @Test
    public void nav() {
        App app = appRepository.findAll().get(0);
        Result<List<EnvClusterInfo>> navResult = appController.nav(app.getId());

        Assert.assertTrue(navResult.success());
    }
}
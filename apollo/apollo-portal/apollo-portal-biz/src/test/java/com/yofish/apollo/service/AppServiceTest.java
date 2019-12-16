package com.yofish.apollo.service;

import com.yofish.apollo.domain.App;
import com.yofish.gary.biz.domain.User;
import com.youyu.common.api.PageData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import static org.junit.Assert.*;

/**
 * @author WangSongJun
 * @date 2019-12-16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yofish.apollo.JpaApplication.class})
public class AppServiceTest {
    @Autowired
    private AppService appService;


    @Test
    public void createApp() {
        App app = App.builder().name("测试项目").appCode("test-app").appOwner(new User(1L)).build();
        App createApp = this.appService.createApp(app);
        Assert.assertTrue(!ObjectUtils.isEmpty(createApp.getId()));
    }

    @Test
    public void findAll() {
        PageData<App> all = this.appService.findAll(PageRequest.of(0, 5));

    }

    @Test
    public void searchByAppCodeOrAppName() {
        PageData<App> pageData = this.appService.searchByAppCodeOrAppName("auth", PageRequest.of(0, 5));

    }

    @Test
    public void updateApp() {
    }
}
package com.yofish.apollo.service;

import com.yofish.apollo.domain.App;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.DepartmentRepository;
import com.yofish.gary.biz.repository.UserRepository;
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
 * @date 2019-12-16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yofish.apollo.JpaApplication.class})
public class AppServiceTest {
    @Autowired
    private AppService appService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void createApp() {
        List<Department> departmentList = this.departmentRepository.findAll();
        Assert.assertTrue(!ObjectUtils.isEmpty(departmentList));

        List<User> allUser = userRepository.findAll();
        Assert.assertTrue(!ObjectUtils.isEmpty(allUser));

        App app = App.builder().name("测试项目").appCode("test-app").appOwner(allUser.get(0)).department(departmentList.get(0)).build();
        App createApp = this.appService.createApp(app);
        Assert.assertTrue(!ObjectUtils.isEmpty(createApp.getId()));
    }

}
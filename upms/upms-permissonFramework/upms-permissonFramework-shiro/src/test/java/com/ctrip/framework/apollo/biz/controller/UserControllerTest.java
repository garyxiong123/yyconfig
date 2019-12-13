package com.ctrip.framework.apollo.biz.controller;

import com.yofish.gary.api.dto.req.UserAddReqDTO;
import com.yofish.gary.api.dto.req.UserLoginReqDTO;
import com.yofish.gary.biz.controller.UserController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/11 下午5:34
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yofish.gary.UmpsShiroApplication.class})
public class UserControllerTest {

    private String username = "gary";
    private String password = "123456";

    @Autowired
    private UserController userController;

    @Before
    public void setUp() throws Exception {
        this.addUser();
    }

    @Test
    public void login() {
        userController.login(createUserLoginDto());
    }

    private UserLoginReqDTO createUserLoginDto() {
        return UserLoginReqDTO.builder().build();
    }

    @Rollback(false)
    @Test
    public void addUser() {

        UserAddReqDTO userAddReqDTO = createUserAddReqDTO();
        userController.add(userAddReqDTO);
    }

    private UserAddReqDTO createUserAddReqDTO() {


        UserAddReqDTO userAddReqDTO = UserAddReqDTO.builder().username(username).password(password).build();
        return userAddReqDTO;
    }
}
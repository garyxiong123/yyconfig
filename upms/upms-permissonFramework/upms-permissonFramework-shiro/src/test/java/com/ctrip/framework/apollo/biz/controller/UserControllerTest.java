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
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
package com.gary.apollo.test.controller;

import apollo.Config;
import apollo.model.ConfigChange;
import apollo.model.ConfigChangeEvent;
import apollo.spring.annotation.ApolloConfig;
import apollo.spring.annotation.ApolloConfigChangeListener;
import apollo.spring.annotation.EnableApolloConfig;
import com.youyu.common.api.Result;
import com.youyu.common.constant.ApplicationInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiongchengwei
 * @date 2018/8/20
 */


/**
 * api 层不定义
 */
@Slf4j
@Api("告警接口")
@RestController
@EnableApolloConfig
public class UserController implements ApplicationContextAware {
    @Autowired
    private HttpServletRequest httpServletRequest;

    private ApplicationContext applicationContext;

//    @Autowired
//    private UserService userService;
    @Value("${dbName:gary}")
    public String dbName;
    @Value("${redis:redis}")
    public String redis;


    @ApolloConfig("application")
    private Config config;

//    @ApolloConfig("testJson")
//    private Config testJsonConfig;



// @Value("${request.timeout:200}")
//    private int timeout;

    @ApolloConfigChangeListener("application")
    private void someOnChange(ConfigChangeEvent changeEvent) {
        changeEvent.changedKeys().forEach(key -> {
            ConfigChange change = changeEvent.getChange(key);
            System.out.println(String.format("Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType()));
        });
    }


//    @PostMapping("/getUserInfo")
//    public Result<UserInfoDto> getUserInfo(@RequestBody UserInfoParam userInfoParam) {
////        userService.selectAll()
////        Transaction t = Cat.getProducer().newTransaction("Exec", "CaiYiMessage");
////        try {
//        System.out.println(ApplicationInfo.getApplicationNameFromHttpContext(httpServletRequest));
////        } catch (Exception e) {
////            t.setStatus(e);
////            throw new BizException("不存在的短信签名", e);
////        } finally {
////            t.complete();
////        }
//        return Result.ok(userService.getUserInfo(userInfoParam.getName()));
//    }


    @GetMapping("/getUserInfo1")
    public Result<String> getUserInfo1(String name, int age) {

        System.out.println(dbName);
        System.out.println("redis:" + redis);

        System.out.println(config);


//        System.out.println(testJsonConfig);

////        userService.selectAll()
//        Transaction t = Cat.getProducer().newTransaction("Exec", "CaiYiMessage");
//        try {
//            System.out.println(ApplicationInfo.getApplicationNameFromHttpContext(httpServletRequest));
//        } catch (Exception e) {
//            t.setStatus(e);
//            throw new BizException("不存在的短信签名", e);
//        } finally {
//            t.complete();
//        }
        return Result.ok(dbName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}

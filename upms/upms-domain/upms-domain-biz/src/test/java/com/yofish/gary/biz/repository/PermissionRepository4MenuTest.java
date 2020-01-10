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
package com.yofish.gary.biz.repository;

import com.yofish.gary.biz.domain.Permission4Menu;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/11 下午3:00
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PermissionRepository4MenuTest {


    @Autowired
    private PermissionRepository4Menu permissionRepository4Menu;
    private String url = "addUser";
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void countByUrl() {
        Permission4Menu permission4Menu = createPermission4Menu();
        permissionRepository4Menu.save(permission4Menu);
        long l = permissionRepository4Menu.countByIframeUrl(url);
        Assert.assertNotNull(l);
    }

    private Permission4Menu createPermission4Menu() {
        Permission4Menu permission4Menu = (Permission4Menu) Permission4Menu.builder().permissionName("新增权限").build();
        permission4Menu.setIframeUrl(url);
        return permission4Menu;
    }
}
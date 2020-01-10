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
package com.yofish.gary.biz.config;

import com.yofish.gary.api.dto.req.UserAddReqDTO;
import com.yofish.gary.api.enums.RoleTypeEnum;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.Role;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.DepartmentRepository;
import com.yofish.gary.biz.repository.RoleRepository;
import com.yofish.gary.biz.repository.UserRepository;
import com.yofish.gary.biz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * @author WangSongJun
 * @date 2019-12-05
 */
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Slf4j
@Component("userDataImport")
@DependsOn("strategyNumBean")
public class DataImport {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UpmsProperties upmsProperties;

    /**
     * 初始化部门、角色、用户数据
     */
    @PostConstruct
    public void importDepartmentAndRoleAndUser() {
        log.info("初始化部门、角色、用户数据...");

        InitProperties init = upmsProperties.getInit();
        if (!init.isInitData()) {
            log.info("系统参数`upms.initData`配置不需要初始化默认数据.");
            return;
        }

        log.info("[1/3] 初始化部门信息...");
        Department department = this.departmentRepository.findByName(init.getDepartment());
        if (ObjectUtils.isEmpty(department)) {
            department = Department.builder().code(init.getDepartmentCode()).name(init.getDepartment()).comment(init.getDepartmentComment()).build();
            this.departmentRepository.save(department);
        }
        log.info("[1/3] 初始化部门信息完成:{}", department.getName());


        log.info("[2/3] 初始化角色信息...");
        Role adminRole = roleRepository.findRoleByRoleName(RoleTypeEnum.ADMIN.name());
        if (ObjectUtils.isEmpty(adminRole)) {
            adminRole = Role.builder().roleName(RoleTypeEnum.ADMIN.name()).remark(RoleTypeEnum.ADMIN.getDesc()).build();
            this.roleRepository.save(adminRole);
        }
        log.info("[2/3] 初始化角色{}完成.", adminRole.getRoleName());

        Role ordinaryUserRole = roleRepository.findRoleByRoleName(RoleTypeEnum.ORDINARY_USER.name());
        if (ObjectUtils.isEmpty(ordinaryUserRole)) {
            ordinaryUserRole = Role.builder().roleName(RoleTypeEnum.ORDINARY_USER.name()).remark(RoleTypeEnum.ORDINARY_USER.getDesc()).build();
            this.roleRepository.save(ordinaryUserRole);
        }
        log.info("[2/3] 初始化角色{}完成.", ordinaryUserRole.getRoleName());


        log.info("[3/3] 初始化用户数据...");
        User defaultAdminUser = this.userRepository.findByUsername(init.getAdminUsername());
        if (isEmpty(defaultAdminUser)) {
            UserAddReqDTO initAdmin = UserAddReqDTO.builder()
                    .remark("初始用户")
                    .username(init.getAdminUsername())
                    .password(init.getAdminPassword())
                    .realName(init.getAdminRealName())
                    .email(init.getAdminEmail())
                    .departmentId(department.getId())
                    .roleIds(Arrays.asList(Long.valueOf(adminRole.getId())))
                    .build();
            this.userService.add(initAdmin);
            log.info("[3/3] 初始化用户完成！ UserName:{},Password:{}", init.getAdminUsername(), init.getAdminPassword());
        } else {
            log.info("[3/3] 初始用户已存在！ UserName:{}", defaultAdminUser.getUsername());
        }

    }
}

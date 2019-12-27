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

    private final String defaultDepartment = "默认部门";
    private final String defaultDepartmentCode = "DefaultDepartment";

    private String adminUsername = "apollo";
    private String adminPassword = "apollo";
    private String adminRealName = "管理员用户";
    private String adminEmail = "apollo@yofish.com";

    /**
     * 初始化部门、角色、用户数据
     */
    @PostConstruct
    public void importDepartmentAndRoleAndUser() {
        log.info("初始化部门、角色、用户数据...");

        log.info("[1/3] 初始化部门信息...");
        Department department = this.departmentRepository.findByName(defaultDepartment);
        if (ObjectUtils.isEmpty(department)) {
            department = Department.builder().code(defaultDepartmentCode).name(defaultDepartment).comment("系统初始化默认部门").build();
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
        User defaultAdminUser = this.userRepository.findByUsername(adminUsername);
        if (isEmpty(defaultAdminUser)) {
            UserAddReqDTO initAdmin = UserAddReqDTO.builder()
                    .remark("初始用户")
                    .username(adminUsername)
                    .password(adminPassword)
                    .realName(adminRealName)
                    .email(adminEmail)
                    .departmentId(department.getId())
                    .roleIds(Arrays.asList(Long.valueOf(adminRole.getId())))
                    .build();
            this.userService.add(initAdmin);
        }

        log.info("[3/3] 初始化用户完成！ UserName:{},Password:{}", adminUsername, adminPassword);

    }
}

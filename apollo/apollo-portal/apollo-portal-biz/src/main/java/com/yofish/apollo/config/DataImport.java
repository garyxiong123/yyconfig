package com.yofish.apollo.config;

import com.yofish.apollo.domain.Department;
import com.yofish.apollo.domain.ServerConfig;
import com.yofish.apollo.enums.ServerConfigKey;
import com.yofish.apollo.repository.DepartmentRepository;
import com.yofish.apollo.repository.ServerConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;

/**
 * @author WangSongJun
 * @date 2019-12-17
 */
@Slf4j
@Component("apolloDataImport")
public class DataImport {
    @Autowired
    private ServerConfigRepository serverConfigRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    private final String activeEvns = "dev,test,pre,prod";
    private final String defaultDepartment = "默认部门";
    private final String defaultDepartmentCode = "DefaultDepartment";


    @PostConstruct
    public void activeDefaultEnvs() {
        log.info("初始化系统配置...");

        log.info("配置可支持的环境列表");
        ServerConfig envConfig = this.serverConfigRepository.findByKey(ServerConfigKey.ApolloPortalEnvs.getKey());
        if (ObjectUtils.isEmpty(envConfig)) {
            envConfig = new ServerConfig(ServerConfigKey.ApolloPortalEnvs.getKey(), activeEvns, "可支持的环境列表");
            this.serverConfigRepository.save(envConfig);
        }
        log.info("可支持的环境列表:{}", envConfig.getValue());
    }

    @PostConstruct
    private void createDefaultDepartment() {
        log.info("初始化部门信息...");
        Department department = this.departmentRepository.findByName(defaultDepartment);
        if (ObjectUtils.isEmpty(department)) {
            department = Department.builder().code(defaultDepartmentCode).name(defaultDepartment).comment("系统初始化默认部门").build();
            this.departmentRepository.save(department);
        }
        log.info("初始化部门信息完成:{}", department.getName());
    }
}

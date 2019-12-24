package com.yofish.apollo.config;

import com.yofish.apollo.domain.*;
import com.yofish.apollo.enums.ServerConfigKey;
import com.yofish.apollo.repository.*;
import framework.apollo.core.enums.ConfigFileFormat;
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
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    @Autowired
    private AppEnvClusterRepository clusterRepository;

    private final String activeEvns = "dev,test,pre,prod";
    private final String defaultDepartment = "默认部门";
    private final String defaultDepartmentCode = "DefaultDepartment";
    private final String defaultAppName = "中台支付";
    private final String defaultAppCode = "payment";

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
    private void createDefaultData() {
        log.info("初始化部门信息...");
        Department department = this.departmentRepository.findByName(defaultDepartment);
        if (!ObjectUtils.isEmpty(department)) {
            return;
        }
        department = Department.builder().code(defaultDepartmentCode).name(defaultDepartment).comment("系统初始化默认部门").build();
        this.departmentRepository.save(department);

        log.info("初始化部门信息完成:{}", department.getName());


        App app = createDefaultApp(department);
        AppNamespace appNamespace = createDefaultAppNamespace(app);
        AppEnvCluster appEnvCluster = createDefaultCluster(app);

        AppEnvClusterNamespace namespace = createDefaultNamespace4Main(appEnvCluster, appNamespace);

    }

    private AppEnvClusterNamespace createDefaultNamespace4Main(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        AppEnvClusterNamespace4Main namespace = new AppEnvClusterNamespace4Main();
        namespace.setAppNamespace(appNamespace);
        namespace.setAppEnvCluster(appEnvCluster);

        namespaceRepository.save(namespace);
        return namespace;
    }

    private AppEnvCluster createDefaultCluster(App app) {
        AppEnvCluster appEnvCluster = AppEnvCluster.builder().app(app).env("test").name("default").build();
        clusterRepository.save(appEnvCluster);
        return appEnvCluster;
    }

    private App createDefaultApp(Department department) {
        App app = App.builder().name(defaultAppName).appCode(defaultAppCode).department(department).build();
        appRepository.save(app);
        return app;
    }

    private AppNamespace createDefaultAppNamespace(App app) {
        AppNamespace appNamespace = new AppNamespace();
        String appNamespaceName = "application";
        appNamespace.setName(appNamespaceName);
        appNamespace.setComment("项目命名空间");
        appNamespace.setFormat(ConfigFileFormat.Properties);
        appNamespace.setApp(app);

        appNamespaceRepository.save(appNamespace);

        return appNamespace;
    }


//
//    private AppEnvClusterNamespace createDefaultAppEnvClusterNamespace(App app) {
//
//        return null;
//    }


}

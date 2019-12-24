package com.yofish.apollo.config;

import com.google.common.collect.Sets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.enums.ServerConfigKey;
import com.yofish.apollo.repository.*;
import com.yofish.apollo.service.AppService;
import com.yofish.apollo.service.ItemService;
import com.yofish.apollo.service.NamespaceBranchService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.UserRepository;
import common.dto.NamespaceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-12-17
 */
@Slf4j
@Order()
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
    private AppEnvClusterNamespace4BranchRepository namespace4BranchRepository;
    @Autowired
    private AppEnvClusterRepository clusterRepository;
    @Autowired
    private AppService appService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ReleaseService releaseService;

    @Autowired
    private NamespaceBranchService namespaceBranchService;

    private final String activeEvns = "dev,test,pre,prod";
    private final String defaultDepartment = "默认部门";
    private final String defaultDepartmentCode = "DefaultDepartment";
    private final String defaultAppName = "中台支付";
    private final String defaultAppCode = "payment";
    private final String defaultNamespaceName = "application";
    private AppEnvClusterNamespace namespace;

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
        AppEnvClusterNamespace4Branch namespace4Branch = createDefaultNamespace4Branch(app);


        createFirstCreateItem(namespace);

        createFirstRelease(namespace);

        createSecondCreateItem(namespace);

        createSecondRelease(namespace);

        createThirdCreateItem(namespace);


    }

    private void createThirdCreateItem(AppEnvClusterNamespace namespace) {
        String releaseName = "releaseName3";
        String releaseComment = "releaseComment3";
        boolean isEmergencyPublish = false;
        releaseService.publish(namespace,releaseName, releaseComment, null,  isEmergencyPublish);
    }

    private void createFirstCreateItem(AppEnvClusterNamespace namespace) {
        CreateItemReq itemReq = CreateItemReq.builder().appEnvClusterNamespaceId(namespace.getId()).key("dbName").lineNum(1).value("payment").build();
        itemService.createItem(itemReq);
    }


    private void createSecondRelease(AppEnvClusterNamespace namespace) {
        String releaseName = "releaseName2";
        String releaseComment = "releaseComment2";
        boolean isEmergencyPublish = false;
        releaseService.publish(namespace,releaseName, releaseComment, null,  isEmergencyPublish);
    }

    private void createSecondCreateItem(AppEnvClusterNamespace namespace) {
        CreateItemReq itemReq = CreateItemReq.builder().appEnvClusterNamespaceId(namespace.getId()).key("password").lineNum(2).value("123456").build();
        itemService.createItem(itemReq);

    }

    private void createFirstRelease(AppEnvClusterNamespace namespace) {
        String releaseName = "releaseName1";
        String releaseComment = "releaseComment1";
        boolean isEmergencyPublish = false;
        releaseService.publish(namespace,releaseName, releaseComment, null,  isEmergencyPublish);

    }

    private AppEnvClusterNamespace4Branch createDefaultNamespace4Branch(App app) {


        namespace = namespaceRepository.findAll().get(0);

        NamespaceDTO namespaceDTO = namespaceBranchService.createBranch(namespace.getId(), "shanghai-DB");
        AppEnvClusterNamespace4Branch namespace4Branch = namespace4BranchRepository.findAll().get(0);
        return namespace4Branch;
    }


    private App createDefaultApp(Department department) {

        App app = App.builder().name(defaultAppName).appCode(defaultAppCode).department(department).build();
        List<User> users = userRepository.findAll();
        app.setAppOwner(users.get(0));

        app.setAppAdmins(Sets.newHashSet(users));
        appService.createApp(app);
        return app;
    }


}

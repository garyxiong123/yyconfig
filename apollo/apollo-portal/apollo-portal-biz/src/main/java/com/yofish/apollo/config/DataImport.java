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
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.DepartmentRepository;
import com.yofish.gary.biz.repository.UserRepository;
import common.dto.NamespaceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private OpenNamespaceTypeRepository openNamespaceTypeRepository;

    private final String activeEvns = "dev,test,pre,prod";
    private final String defaultAppName = "中台支付";
    private final String defaultAppCode = "payment";
    private final String defaultNamespaceName = "application";
    private final List<String> openNamespaceTypeList = Arrays.asList("MySql", "Redis", "Other");
    private AppEnvClusterNamespace namespace;

    /**
     * 数据的原子性规则
     */
    @PostConstruct
    public void activeDefaultEnvsAndInitData() {
        log.info("初始化系统配置...");

        log.info("配置可支持的环境列表");
        ServerConfig envConfig = this.serverConfigRepository.findByKey(ServerConfigKey.ApolloPortalEnvs.getKey());
        if (!ObjectUtils.isEmpty(envConfig)) {
            return;
        }
        envConfig = new ServerConfig(ServerConfigKey.ApolloPortalEnvs.getKey(), activeEvns, "可支持的环境列表");
        this.serverConfigRepository.save(envConfig);
        log.info("可支持的环境列表:{}", envConfig.getValue());


        log.info("初始化默认项目...");

        App app = createDefaultApp();


        AppEnvClusterNamespace4Branch namespace4Branch = createDefaultNamespace4Branch(app);


        createFirstCreateItem(namespace);

        createFirstRelease(namespace);

        createSecondCreateItem(namespace);

        createSecondRelease(namespace);

        createThirdCreateItem(namespace);

        createItem4Branch(namespace4Branch);

        createRelease4Branch(namespace4Branch);

    }

    @PostConstruct
    public void initDefaultOpenNamespaceType() {
        log.info("初始化公开命名空间类型...");

        long count = openNamespaceTypeRepository.count();
        if (count < 1) {
            List<OpenNamespaceType> openNamespaceTypes = openNamespaceTypeList.stream().map(name -> OpenNamespaceType.builder().name(name).build()).collect(Collectors.toList());
            openNamespaceTypeRepository.saveAll(openNamespaceTypes);
        }
        log.info("初始化公开命名空间类型完成.");
    }

    private void createRelease4Branch(AppEnvClusterNamespace4Branch namespace4Branch) {
        String releaseName = "releaseName-Branch";
        String releaseComment = "releaseComment-Branch";
        boolean isEmergencyPublish = false;
        releaseService.publish(namespace4Branch, releaseName, releaseComment, null, isEmergencyPublish);
    }

    private void createItem4Branch(AppEnvClusterNamespace4Branch namespace4Branch) {
        CreateItemReq itemReq = CreateItemReq.builder().appEnvClusterNamespaceIds(Arrays.asList(namespace4Branch.getId())).key("dbName-branch").lineNum(1).value("payment-branch").build();
        itemService.createItem(itemReq);
    }

    private void createThirdCreateItem(AppEnvClusterNamespace namespace) {
        CreateItemReq itemReq = CreateItemReq.builder().appEnvClusterNamespaceIds(Arrays.asList(namespace.getId())).key("userName-toPublish").lineNum(4).value("garyxiong-toPublish").build();
        itemService.createItem(itemReq);
    }

    private void createFirstCreateItem(AppEnvClusterNamespace namespace) {
        CreateItemReq itemReq = CreateItemReq.builder().appEnvClusterNamespaceIds(Arrays.asList(namespace.getId())).key("dbName").lineNum(1).value("payment").build();
        itemService.createItem(itemReq);
    }


    private void createSecondRelease(AppEnvClusterNamespace namespace) {
        String releaseName = "releaseName2";
        String releaseComment = "releaseComment2";
        boolean isEmergencyPublish = false;
        releaseService.publish(namespace, releaseName, releaseComment, null, isEmergencyPublish);
    }

    private void createSecondCreateItem(AppEnvClusterNamespace namespace) {
        CreateItemReq itemReq = CreateItemReq.builder().appEnvClusterNamespaceIds(Arrays.asList(namespace.getId())).key("password").lineNum(2).value("123456").build();
        itemService.createItem(itemReq);

    }

    private void createFirstRelease(AppEnvClusterNamespace namespace) {
        String releaseName = "releaseName1";
        String releaseComment = "releaseComment1";
        boolean isEmergencyPublish = false;
        releaseService.publish(namespace, releaseName, releaseComment, null, isEmergencyPublish);

    }

    private AppEnvClusterNamespace4Branch createDefaultNamespace4Branch(App app) {


        namespace = namespaceRepository.findAll().get(0);

        NamespaceDTO namespaceDTO = namespaceBranchService.createBranch(namespace.getId(), "shanghai-DB");
        AppEnvClusterNamespace4Branch namespace4Branch = namespace4BranchRepository.findAll().get(0);
        return namespace4Branch;
    }


    private App createDefaultApp() {

        List<Department> departmentList = departmentRepository.findAll();
        App app = App.builder().name(defaultAppName).appCode(defaultAppCode).department(departmentList.get(0)).build();
        List<User> users = userRepository.findAll();
        app.setAppOwner(users.get(0));
        app.setAppAdmins(Sets.newHashSet(users));

        appService.createApp(app);
        return app;
    }


}

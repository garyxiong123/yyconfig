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
package com.yofish.apollo.component.init;

import com.google.common.collect.Sets;
import com.yofish.apollo.component.config.ServerConfigKey;
import com.yofish.apollo.component.config.ServerConfigProperties;
import com.yofish.apollo.component.config.SystemInitConfigKey;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.api.dto.CreateItemReq;
import com.yofish.apollo.model.AppModel;
import com.yofish.apollo.repository.*;
import com.yofish.apollo.service.AppService;
import com.yofish.apollo.service.ItemService;
import com.yofish.apollo.service.NamespaceBranchService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.DepartmentRepository;
import com.yofish.gary.biz.repository.UserRepository;
import com.yofish.yyconfig.common.common.dto.NamespaceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ServerConfigProperties configProperties;

    private AppEnvClusterNamespace namespace;


    @PostConstruct
    public void activeDefaultEnvsAndInitData() {
        log.info("初始化系统配置...");

        log.info("加载配置信息执行初始化...");
        Map<ServerConfigKey, String> serverConfig = configProperties.getServerConfig();

        serverConfig.forEach((k, v) -> {
            if (this.serverConfigRepository.count(Example.of(ServerConfig.builder().key(k).build())) == 0) {
                log.info("初始化：{}:{}", k, v);
                ServerConfig config = new ServerConfig(k, configProperties.get(k), "初始化配置");
                this.serverConfigRepository.save(config);
            }
        });
        log.info("系统配置初始化完成.");


        log.info("初始化默认项目...");

        if (appRepository.count() > 0) {
            log.info("已有项目信息，不再执行初始化默认项目！");
            return;
        }
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
            List<OpenNamespaceType> openNamespaceTypes = Arrays.asList(configProperties.get(SystemInitConfigKey.OPEN_NAMESPACE_TYPES).split(",")).stream().map(name -> OpenNamespaceType.builder().name(name).build()).collect(Collectors.toList());
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
        App app = App.builder()
                .name(configProperties.get(SystemInitConfigKey.DEFAULT_APP_NAME))
                .appCode(configProperties.get(SystemInitConfigKey.DEFAULT_APP_CODE))
                .department(departmentList.get(0)).build();
        List<User> users = userRepository.findAll();
//        app.setAppOwner(users.get(0));
//        app.setAppAdmins(Sets.newHashSet(users));
        AppModel appModel = AppModel.builder().orgId(departmentList.get(0).getId()).admins(Sets.newHashSet(users.get(0).getId())).name(configProperties.get(SystemInitConfigKey.DEFAULT_APP_NAME)).appCode(configProperties.get(SystemInitConfigKey.DEFAULT_APP_CODE)).ownerId(users.get(0).getId()).build();
        appService.createApp(appModel);
        return app;
    }


}

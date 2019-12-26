package com.yofish.apollo;

import com.google.common.collect.Sets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.repository.*;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.DepartmentRepository;
import com.yofish.gary.biz.repository.UserRepository;
import framework.apollo.core.enums.Env;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 下午2:44
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yofish.apollo.JpaApplication.class})
public class AppRepositoryTest {
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Before
    public void setUp() throws Exception {


    }




    @Test
    public void addApp() {
        App app = createApp();
        App app1 = appRepository.save(app);
        Assert.assertNotNull(app1);
    }

    @Test
    public void addApp4Select() {
        App app = createApp4Select();
        App app1 = appRepository.save(app);
        Assert.assertNotNull(app1);
    }

    @Test
    public void addApp4SelectForId() {
        App app = createApp4Select4Id();
        App app1 = appRepository.save(app);
        Assert.assertNotNull(app1);
    }



    @Test
    public void addApp4Wrong() {
        App app = createApp4Wrong();
        App app1 = appRepository.save(app);
        Assert.assertNotNull(app1);
    }

    @Test
    public void addNamespace() {
        AppNamespace namespace = createNamespace();
        appNamespaceRepository.save(namespace);
    }

    @Test
    public void addCluster() {
        AppEnvCluster appEnvCluster = createCluster();
        appEnvClusterRepository.save(appEnvCluster);
    }

    @Test
    public void addClusterNamespace() {
        AppEnvClusterNamespace appEnvClusterNamespace = createClusterNamespace();
        appEnvClusterNamespaceRepository.save(appEnvClusterNamespace);
    }

    @Test
    public void addItem() {
        Item item = createItem();
        itemRepository.save(item);
        Assert.assertNotNull(item);
    }


    @Test
    public void addCommit() {


    }

    private App createApp() {
        User user = createUser();
        Set<User> users = Sets.newHashSet(user);
        Department department = Department.builder().name("技术平台").build();
        App app = App.builder().department(department).name("中台支付").build();
        app.setAppAdmins(users);
        app.setAppOwner(user);
        return app;
    }

    private App createApp4Select() {
        User user = User.builder().username("zhangsan").build();
        userRepository.save(user);
        Set<User> users = new HashSet<>(userRepository.findAll()) ;
//        Department department =   Department.builder().name("caigou").build();
        App app = App.builder().name("中台支付").build();
        app.setAppAdmins(users);
        app.setAppOwner(users.iterator().next());
        return app;
    }


    private App createApp4Select4Id() {
        User user = User.builder().username("zhangsan").build();
        userRepository.save(user);
        Set<User> users = new HashSet<>(userRepository.findAll()) ;
        users.iterator().next().setUsername(null);
//        Department department =   Department.builder().name("caigou").build();
        App app = App.builder().name("中台支付").build();
        app.setAppAdmins(users);
        app.setAppOwner(users.iterator().next());
        return app;
    }


    private App createApp4Wrong() {
        User user = User.builder().build();
        user.setId(3L);
        Set<User> users = Sets.newHashSet(user);
        Department department =   Department.builder().id(5L).build();
        App app = App.builder().department(department).name("中台支付").build();
        app.setAppAdmins(users);
        app.setAppOwner(user);
        return app;
    }

    private AppNamespace createNamespace() {
        App app = createApp();

        AppNamespace appNamespace = new AppNamespace();
        appNamespace.setApp(app);
        appNamespace.setName("application");
        return appNamespace;
    }

    private AppEnvClusterNamespace createClusterNamespace() {
        AppEnvCluster appEnvCluster = createCluster();
        AppNamespace namespace = createNamespace();
        return AppEnvClusterNamespace.builder().appEnvCluster(appEnvCluster).appNamespace(namespace).build();
    }


    private AppEnvCluster createCluster() {
        App app = createApp();
        Env env = Env.TEST;
        return AppEnvCluster.builder().app(app).env(env.name()).build();
    }

    private Item createItem() {
        AppEnvClusterNamespace appEnvClusterNamespace = createClusterNamespace();
        return Item.builder().key("kafka.ur.").appEnvClusterNamespace(appEnvClusterNamespace).comment("kafka地址").value("www.abc.com").build();
    }

    private User createUser() {
        User user = User.builder().username("gary").build();
        return user;
    }


}
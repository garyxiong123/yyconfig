package controller;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
//import com.yofish.apollo.controller.ReleaseController;
import com.yofish.apollo.controller.ReleaseController;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.ReleaseDTO;
import com.yofish.apollo.message.Topics;
import com.yofish.apollo.model.model.NamespaceReleaseModel;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.ReleaseService;
import common.dto.AppDTO;
import common.dto.ClusterDTO;
import common.dto.ItemDTO;
import common.dto.NamespaceDTO;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.Env;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ReleaseControllerTest extends AbstractControllerTest {

    @Autowired
    ReleaseRepository releaseRepository;
    @Autowired
    ReleaseController releaseController;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;

    @Test
    public void testRelease4Main() {
        AppEnvClusterNamespace4Main namespace = createNamespace4Main();
        namespaceRepository.save(namespace);
        NamespaceReleaseModel namespaceReleaseModel = NamespaceReleaseModel.builder().AppEnvClusterNamespaceId(namespace.getId()).build();
        releaseController.createRelease(namespaceReleaseModel);
    }

    private AppEnvClusterNamespace4Main createNamespace4Main() {
        AppEnvClusterNamespace4Main appEnvClusterNamespace4Main = createAppEnvClusterNamespace4Main();
        return appEnvClusterNamespace4Main;
    }

    private AppEnvClusterNamespace4Main createAppEnvClusterNamespace4Main() {

        AppEnvClusterNamespace4Main namespace4Main = new AppEnvClusterNamespace4Main();
        App app = createApp();
        namespace4Main.setAppEnvCluster(createAppEnvCluster(app));
        namespace4Main.setAppNamespace(createAppNamespace(app));

        return namespace4Main;
    }

    private App createApp() {
        return App.builder().appCode("middleground").name("中台支付").build();
    }

    private AppEnvCluster createAppEnvCluster(App app) {

        return AppEnvCluster.builder().app(app).name("default集群").env(Env.DEV.name()).build();
    }


    private AppNamespace createAppNamespace(App app) {
        AppNamespace appNamespace = new AppNamespace();
        appNamespace.setApp(app);
        appNamespace.setName("DB-config");
        return appNamespace;
    }

    @Test
    public void testRelease4Branch() {

    }

    @Test
    public void testRelease4Rollback() {

    }

    @Test
//  @Sql(scripts = "/controller/test-release.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
//  @Sql(scripts = "/controller/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    public void testReleaseBuild() {
        String appId = "someAppId";
        AppDTO app = restTemplate.getForObject("http://localhost:" + port + "/apps/" + appId, AppDTO.class);

        ClusterDTO cluster = restTemplate.getForObject("http://localhost:" + port + "/apps/" + app.getAppId() + "/clusters/default",
                ClusterDTO.class);

        NamespaceDTO namespace = restTemplate.getForObject("http://localhost:" + port + "/apps/" + app.getAppId()
                + "/clusters/" + cluster.getName() + "/namespaces/application", NamespaceDTO.class);

        Assert.assertEquals("someAppId", app.getAppId());
        Assert.assertEquals("default", cluster.getName());
        Assert.assertEquals("application", namespace.getNamespaceName());

        ItemDTO[] items =
                restTemplate.getForObject(
                        "http://localhost:" + port + "/apps/" + app.getAppId() + "/clusters/"
                                + cluster.getName() + "/namespaces/" + namespace.getNamespaceName() + "/items",
                        ItemDTO[].class);
        Assert.assertEquals(3, items.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("name", "someReleaseName");
        parameters.add("comment", "someComment");
        parameters.add("operator", "test");
        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<MultiValueMap<String, String>>(parameters, headers);
        ResponseEntity<ReleaseDTO> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/apps/" + app.getAppId() + "/clusters/" + cluster.getName()
                        + "/namespaces/" + namespace.getNamespaceName() + "/releases",
                entity, ReleaseDTO.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        ReleaseDTO release = response.getBody();
        Assert.assertEquals("someReleaseName", release.getName());
        Assert.assertEquals("someComment", release.getComment());
        Assert.assertEquals("someAppId", release.getAppId());
        Assert.assertEquals("default", release.getClusterName());
        Assert.assertEquals("application", release.getNamespaceName());

        Map<String, String> configurations = new HashMap<String, String>();
        configurations.put("k1", "v1");
        configurations.put("k2", "v2");
        configurations.put("k3", "v3");
        Gson gson = new Gson();
        Assert.assertEquals(gson.toJson(configurations), release.getConfigurations());
    }

//  @Test
//  public void testMessageSendAfterBuildRelease() throws Exception {
//    String someAppId = "someAppId";
//    String someNamespaceName = "someNamespace";
//    String someCluster = "someCluster";
//    String someName = "someName";
//    String someComment = "someComment";
//    String someUserName = "someUser";
//
//    NamespaceService someNamespaceService = mock(NamespaceService.class);
//    ReleaseService someReleaseService = mock(ReleaseService.class);
//    MessageSender someMessageSender = mock(MessageSender.class);
//    Namespace someNamespace = mock(Namespace.class);
//
//    ReleaseController releaseController = new ReleaseController();
//    ReflectionTestUtils.setField(releaseController, "releaseService", someReleaseService);
//    ReflectionTestUtils.setField(releaseController, "namespaceService", someNamespaceService);
//    ReflectionTestUtils.setField(releaseController, "messageSender", someMessageSender);
//
//    when(someNamespaceService.findOne(someAppId, someCluster, someNamespaceName))
//        .thenReturn(someNamespace);
//
//    releaseController
//        .publish(someAppId, someCluster, someNamespaceName, someName, someComment, "test", false);
//
//    verify(someMessageSender, times(1))
//        .sendMessage(Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)
//                .join(someAppId, someCluster, someNamespaceName),
//            Topics.APOLLO_RELEASE_TOPIC);
//
//  }
}

package controller;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
//import com.yofish.apollo.controller.ReleaseController;
import com.yofish.apollo.DomainCreate;
import com.yofish.apollo.controller.ItemController;
import com.yofish.apollo.controller.ReleaseController;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ReleaseDTO;
import com.yofish.apollo.message.Topics;
import com.yofish.apollo.model.model.NamespaceReleaseModel;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.CommitService;
import com.yofish.apollo.service.ReleaseService;
import common.dto.AppDTO;
import common.dto.ClusterDTO;
import common.dto.ItemDTO;
import common.dto.NamespaceDTO;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.Env;
import net.bytebuddy.asm.Advice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import static com.yofish.apollo.DomainCreate.createAppEnvClusterNamespace4Main;
import static com.yofish.apollo.DomainCreate.createNamespace4Main;
import static org.mockito.Mockito.*;

public class ReleaseControllerTest extends AbstractControllerTest {

    @Autowired
    ReleaseRepository releaseRepository;
    @Autowired
    ReleaseController releaseController;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    private AppEnvClusterNamespace4Main namespace;

    @Autowired
    private CommitService commitService;
    @Autowired
    private ItemController itemController;

    @Before
    public void setUp() {
        namespace = createNamespace4Main();
        namespaceRepository.save(namespace);
    }

    @Test
    public void testRelease4Main() {
//        CreateItemReq req = createItemReq();
//        itemController.createItem(req);

        NamespaceReleaseModel namespaceReleaseModel = NamespaceReleaseModel.builder().releaseTitle("测试发布标题").releaseComment("测试发布").AppEnvClusterNamespaceId(namespace.getId()).build();
        releaseController.createRelease(namespaceReleaseModel);

    }

    private CreateItemReq createItemReq() {
        Long namespaceId = namespace.getId();
        CreateItemReq itemReq = DomainCreate.createItemReq();
        itemReq.setAppEnvClusterNamespaceId(namespaceId);
        return itemReq;
    }


    @Test
    public void testRelease4MainWithBranch() {
        AppEnvClusterNamespace4Main namespace = createNamespace4MainWithBranch();
        namespaceRepository.save(namespace);
        NamespaceReleaseModel namespaceReleaseModel = NamespaceReleaseModel.builder().releaseTitle("测试发布标题").releaseComment("测试发布").AppEnvClusterNamespaceId(namespace.getId()).build();
        releaseController.createRelease(namespaceReleaseModel);
    }

    @Test
    public void testRelease4Branch() {

    }


    @Test
    public void testRollback() throws AccessDeniedException {
        Release release = createTwoRelease();

        releaseController.rollback(release.getId());
    }

    private Release createTwoRelease() {
        return null;
    }


    private AppEnvClusterNamespace4Main createNamespace4MainWithBranch() {
        AppEnvClusterNamespace4Main appEnvClusterNamespace4Main = createAppEnvClusterNamespace4Main();
        createAppEnvClusterNamespace4Branch(appEnvClusterNamespace4Main);

        return null;
    }

    private void createAppEnvClusterNamespace4Branch(AppEnvClusterNamespace4Main appEnvClusterNamespace4Main) {

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

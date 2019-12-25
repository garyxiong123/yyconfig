package controller;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
//import com.yofish.apollo.controller.ReleaseController;
import com.yofish.apollo.DomainCreate;
import com.yofish.apollo.component.PermissionValidator;
import com.yofish.apollo.controller.ItemController;
import com.yofish.apollo.controller.ReleaseController;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ReleaseDTO;
import com.yofish.apollo.dto.ReleaseHistoryDTO;
import com.yofish.apollo.message.Topics;
import com.yofish.apollo.model.bo.ReleaseBO;
import com.yofish.apollo.model.bo.ReleaseHistoryBO;
import com.yofish.apollo.model.model.NamespaceReleaseModel;
import com.yofish.apollo.model.vo.ReleaseCompareResult;
import com.yofish.apollo.repository.*;
import com.yofish.apollo.service.CommitService;
import com.yofish.apollo.service.ReleaseHistoryService;
import com.yofish.apollo.service.ReleaseService;
import com.youyu.common.api.Result;
import com.youyu.common.exception.BizException;
import common.dto.AppDTO;
import common.dto.ClusterDTO;
import common.dto.ItemDTO;
import common.dto.NamespaceDTO;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.Env;
import net.bytebuddy.asm.Advice;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private AppEnvClusterNamespace4MainRepository namespaceRepository4Main;

    private AppEnvClusterNamespace4Main namespace;
    @Autowired
    private CommitRepository commitRepository;
    @Autowired
    private CommitService commitService;
    @Autowired
    private ItemController itemController;
    @Autowired
    private ReleaseHistoryRepository releaseHistoryRepository;
    @Autowired
    private ReleaseHistoryService releaseHistoryService;

    private PermissionValidator permissionValidator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    //    @Rollback()
    @Before
    public void setUp() {
        namespace = namespaceRepository4Main.findAll().get(0);

        permissionValidator = mock(PermissionValidator.class);
        ReflectionTestUtils.setField(releaseController, "permissionValidator", permissionValidator);
        when(permissionValidator.hasReleaseNamespacePermission(namespace.getAppNamespace().getApp().getAppCode())).thenReturn(true);
    }

    @Test
    public void testRelease4MainWithBranch() {
        CreateItemReq req = createItemReq();
        itemController.createItem(req);

        NamespaceReleaseModel namespaceReleaseModel = NamespaceReleaseModel.builder().releaseTitle("测试发布标题").releaseComment("测试发布").AppEnvClusterNamespaceId(namespace.getId()).build();
        common.dto.ReleaseDTO release = releaseController.createRelease(namespaceReleaseModel).data;

        Iterable<ReleaseHistory> releaseHistoryRepositories = releaseHistoryRepository.findAll();
        Page<ReleaseHistoryDTO> namespaceReleaseHistory = releaseHistoryService.findNamespaceReleaseHistory(namespace.getId(), 0, 10);

        Assert.assertNotNull(releaseHistoryRepositories);

    }

    private CreateItemReq createItemReq() {
        Long namespaceId = namespace.getId();
        CreateItemReq itemReq = DomainCreate.createItemReq();
        itemReq.setAppEnvClusterNamespaceIds(Arrays.asList(namespaceId));
        itemReq.setComment("加上一个字段");
        return itemReq;
    }


    @Test
    public void testRelease4Branch() {
        NamespaceReleaseModel releaseModel4Main = NamespaceReleaseModel.builder().releaseTitle("测试发布标题").releaseComment("测试发布").AppEnvClusterNamespaceId(namespace.getId()).build();
        common.dto.ReleaseDTO release = releaseController.createRelease(releaseModel4Main).data;

        AppEnvClusterNamespace4Branch namespace4Branch = namespace.getBranchNamespace();
        NamespaceReleaseModel releaseModel4Branch = NamespaceReleaseModel.builder().releaseTitle("测试发布标题").releaseComment("分支测试发布").AppEnvClusterNamespaceId(namespace4Branch.getId()).build();
        releaseController.createRelease(releaseModel4Branch);
    }


    @Test
    public void testRollback() throws AccessDeniedException {

        Release release = namespace.findLatestActiveRelease();

        releaseController.rollback(release.getId());
    }

    @Test(expected = BizException.class)
    public void testRollback4Branch() throws AccessDeniedException {

        Release release4Branch = namespace.getBranchNamespace().findLatestActiveRelease();

        releaseController.rollback(release4Branch.getId());
    }

    @Test
    public void testFindAllRelease() throws AccessDeniedException {
        Pageable page = PageRequest.of(0,5);
        Result<List<ReleaseBO>> allReleases = releaseController.findAllReleases(namespace.getId(), 0, 5);

        Assert.assertNotNull(allReleases.getData());
    }

    @Test
    public void testFindActiveRelease() throws AccessDeniedException {
        Pageable page = PageRequest.of(0,2);
        Result<List<common.dto.ReleaseDTO>> allReleases = releaseController.findActiveReleases(namespace.getId(), 0, 2);

        Assert.assertNotNull(allReleases.getData());
    }


    @Test
    public void testCompareRelease() throws AccessDeniedException {
        Pageable page = PageRequest.of(0,2);
        List<common.dto.ReleaseDTO> allReleases = releaseController.findActiveReleases(namespace.getId(), 0, 2).data;

        Result<ReleaseCompareResult> releaseCompareResultResult = releaseController.compareRelease( allReleases.get(1).getId(), allReleases.get(0).getId());

        Assert.assertNotNull(releaseCompareResultResult);
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

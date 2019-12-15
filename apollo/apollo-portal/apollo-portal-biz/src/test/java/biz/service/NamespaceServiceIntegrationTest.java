//package com.ctrip.framework.apollo.biz.service;
//
//import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
//import com.ctrip.framework.apollo.biz.entity.*;
//import com.ctrip.framework.apollo.biz.repository.InstanceConfigRepository;
//import com.ctrip.framework.apollo.common.entity.AppNamespace;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.context.jdbc.Sql;
//
//import java.util.List;
//
//import static org.junit.Assert.*;
//
//public class NamespaceServiceIntegrationTest extends AbstractIntegrationTest {
//
//
//  @Autowired
//  private NamespaceService namespaceService;
//  @Autowired
//  private ItemService itemService;
//  @Autowired
//  private CommitService commitService;
//  @Autowired
//  private AppNamespaceService appNamespaceService;
//  @Autowired
//  private ClusterService clusterService;
//  @Autowired
//  private ReleaseService releaseService;
//  @Autowired
//  private ReleaseHistoryService releaseHistoryService;
//  @Autowired
//  private InstanceConfigRepository instanceConfigRepository;
//
//  private String testApp = "testApp";
//  private String testCluster = "default";
//  private String testChildCluster = "child-cluster";
//  private String testPrivateNamespace = "application";
//  private String testUser = "apollo";
//
//  @Test
//  @Sql(scripts = "/sql/appNamespace-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//  @Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//  public void testDeleteNamespace() {
//
//    Namespace appNamespace = new Namespace();
//    appNamespace.setAppId(testApp);
//    appNamespace.setClusterName(testCluster);
//    appNamespace.setNamespaceName(testPrivateNamespace);
//    appNamespace.setId(1);
//
//    namespaceService.deleteNamespace(appNamespace, testUser);
//
//    List<Item> items = itemService.findItemsWithoutOrdered(testApp, testCluster, testPrivateNamespace);
//    List<Commit> commits = commitService.find(testApp, testCluster, testPrivateNamespace, new PageRequest(0, 10));
//    AppNamespace appNamespace = appNamespaceService.findOne(testApp, testPrivateNamespace);
//    List<Cluster> childClusters = clusterService.findChildClusters(testApp, testCluster);
//    InstanceConfig instanceConfig = instanceConfigRepository.findOne(1L);
//    List<Release> parentNamespaceReleases = releaseService.findActiveReleases(testApp, testCluster,
//                                                                              testPrivateNamespace,
//                                                                              new PageRequest(0, 10));
//    List<Release> childNamespaceReleases = releaseService.findActiveReleases(testApp, testChildCluster,
//                                                                             testPrivateNamespace,
//                                                                             new PageRequest(0, 10));
//    Page<ReleaseHistory> releaseHistories =
//        releaseHistoryService
//            .findReleaseHistoriesByNamespace(testApp, testCluster, testPrivateNamespace, new PageRequest(0, 10));
//
//    assertEquals(0, items.size());
//    assertEquals(0, commits.size());
//    assertNotNull(appNamespace);
//    assertEquals(0, childClusters.size());
//    assertEquals(0, parentNamespaceReleases.size());
//    assertEquals(0, childNamespaceReleases.size());
//    assertTrue(!releaseHistories.hasContent());
//    assertNull(instanceConfig);
//  }
//
//}

//package com.ctrip.framework.apollo.biz.service;
//
//import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
//import com.yofish.apollo.domain.App;
//import com.yofish.apollo.repository.AppRepository;
//import com.yofish.apollo.service.AppNamespaceService;
//import com.yofish.apollo.service.NamespaceService;
//import framework.apollo.core.ConfigConsts;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Date;
//import java.util.List;
//
//public class AdminServiceTest extends AbstractIntegrationTest {
//
//  @Autowired
//  private AdminService adminService;
//
//  @Autowired
//  private AuditService auditService;
//
//  @Autowired
//  private AppRepository appRepository;
//
//  @Autowired
//  private ClusterService clusterService;
//
//  @Autowired
//  private NamespaceService namespaceService;
//
//  @Autowired
//  private AppNamespaceService appNamespaceService;
//
//  @Test
//  public void testCreateNewApp() {
//    String appId = "someAppId";
//    App app = new App();
//    app.setAppId(appId);
//    app.setName("someAppName");
//    String owner = "someOwnerName";
//    app.setOwnerName(owner);
//    app.setOwnerEmail("someOwnerName@ctrip.com");
//    app.setDataChangeCreatedBy(owner);
//    app.setDataChangeLastModifiedBy(owner);
//    app.setDataChangeCreatedTime(new Date());
//
//    app = adminService.createNewApp(app);
//    Assert.assertEquals(appId, app.getAppId());
//
//    List<Cluster> clusters = clusterService.findParentClusters(app.getAppId());
//    Assert.assertEquals(1, clusters.size());
//    Assert.assertEquals(ConfigConsts.CLUSTER_NAME_DEFAULT, clusters.get(0).getName());
//
//    List<Namespace> namespaces = namespaceService.findNamespaces(appId, clusters.get(0).getName());
//    Assert.assertEquals(1, namespaces.size());
//    Assert.assertEquals(ConfigConsts.NAMESPACE_APPLICATION, namespaces.get(0).getNamespaceName());
//
//    List<Audit> audits = auditService.findByOwner(owner);
//    Assert.assertEquals(4, audits.size());
//  }
//
//  @Test(expected = ServiceException.class)
//  public void testCreateDuplicateApp() {
//    String appId = "someAppId";
//    App app = new App();
//    app.setAppId(appId);
//    app.setName("someAppName");
//    String owner = "someOwnerName";
//    app.setOwnerName(owner);
//    app.setOwnerEmail("someOwnerName@ctrip.com");
//    app.setDataChangeCreatedBy(owner);
//    app.setDataChangeLastModifiedBy(owner);
//    app.setDataChangeCreatedTime(new Date());
//
//    appRepository.save(app);
//
//    adminService.createNewApp(app);
//  }
//
//  @Test
//  public void testDeleteApp() {
//    String appId = "someAppId";
//    App app = new App();
//    app.setAppId(appId);
//    app.setName("someAppName");
//    String owner = "someOwnerName";
//    app.setOwnerName(owner);
//    app.setOwnerEmail("someOwnerName@ctrip.com");
//    app.setDataChangeCreatedBy(owner);
//    app.setDataChangeLastModifiedBy(owner);
//    app.setDataChangeCreatedTime(new Date());
//
//    app = adminService.createNewApp(app);
//
//    Assert.assertEquals(appId, app.getAppId());
//
//    Assert.assertEquals(1, appNamespaceService.findByAppId(appId).size());
//
//    Assert.assertEquals(1, clusterService.findClusters(appId).size());
//
//    Assert.assertEquals(1, namespaceService.findNamespaces(appId, ConfigConsts.CLUSTER_NAME_DEFAULT).size());
//
//    adminService.deleteApp(app, owner);
//
//    Assert.assertEquals(0, appNamespaceService.findByAppId(appId).size());
//
//    Assert.assertEquals(0, clusterService.findClusters(appId).size());
//
//    Assert
//        .assertEquals(0, namespaceService.findByAppIdAndNamespaceName(appId, ConfigConsts.CLUSTER_NAME_DEFAULT).size());
//  }
//
//}

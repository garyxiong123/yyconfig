/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.yofish.apollo.openapi.service;

//import com.ctrip.framework.apollo.common.exception.BizException;
//import com.ctrip.framework.apollo.openapi.entity.Consumer;
//import com.ctrip.framework.apollo.openapi.entity.ConsumerAudit;
//import com.ctrip.framework.apollo.openapi.entity.ConsumerRole;
//import com.ctrip.framework.apollo.openapi.entity.ConsumerToken;
//import com.ctrip.framework.apollo.openapi.repository.ConsumerAuditRepository;
//import com.ctrip.framework.apollo.openapi.repository.ConsumerRepository;
//import com.ctrip.framework.apollo.openapi.repository.ConsumerRoleRepository;
//import com.ctrip.framework.apollo.openapi.repository.ConsumerTokenRepository;
//import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
//import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
//import com.ctrip.framework.apollo.portal.entity.po.Role;
//import com.ctrip.framework.apollo.portal.repository.RoleRepository;
//import com.ctrip.framework.apollo.portal.service.RolePermissionService;
//import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
//import com.ctrip.framework.apollo.portal.spi.UserService;
//import com.ctrip.framework.apollo.portal.util.RoleUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.yofish.apollo.domain.App;
import com.yofish.apollo.openapi.entity.Consumer;
import com.yofish.apollo.openapi.entity.ConsumerAudit;
import com.yofish.apollo.openapi.entity.ConsumerToken;
import com.yofish.apollo.openapi.repository.ConsumerAuditRepository;
import com.yofish.apollo.openapi.repository.ConsumerRepository;
//import com.yofish.apollo.openapi.repository.ConsumerRoleRepository;
import com.yofish.apollo.openapi.repository.ConsumerTokenRepository;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.apollo.service.PortalConfig;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.RoleRepository;
import com.yofish.gary.biz.service.UserService;
import com.youyu.common.exception.BizException;
import com.youyu.common.helper.YyRequestInfoHelper;
import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class ConsumerService {

  private static final FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");
  private static final Joiner KEY_JOINER = Joiner.on("|");

//  private final UserInfoHolder userInfoHolder;
  private final ConsumerTokenRepository consumerTokenRepository;
  private final ConsumerRepository consumerRepository;
  private final ConsumerAuditRepository consumerAuditRepository;
//  private final ConsumerRoleRepository consumerRoleRepository;
  private final PortalConfig portalConfig;
//  private final RolePermissionService rolePermissionService;
  private final UserService userService;
  private final AppRepository appRepository;

  public ConsumerService(
//      final UserInfoHolder userInfoHolder,
      final ConsumerTokenRepository consumerTokenRepository,
      final ConsumerRepository consumerRepository,
      final ConsumerAuditRepository consumerAuditRepository,
//      final ConsumerRoleRepository consumerRoleRepository,
      final PortalConfig portalConfig,
//      final RolePermissionService rolePermissionService,
      final UserService userService,
      final AppRepository appRepository) {
//    this.userInfoHolder = userInfoHolder;
    this.consumerTokenRepository = consumerTokenRepository;
    this.consumerRepository = consumerRepository;
    this.consumerAuditRepository = consumerAuditRepository;
//    this.consumerRoleRepository = consumerRoleRepository;
    this.portalConfig = portalConfig;
//    this.rolePermissionService = rolePermissionService;
    this.userService = userService;
    this.appRepository = appRepository;
  }


  public Consumer createConsumer(Consumer consumer) {
    String appId = consumer.getAppId();

    Consumer managedConsumer = consumerRepository.findByAppId(appId);
    if (managedConsumer != null) {
      throw new BizException("Consumer already exist");
    }

    String ownerName = consumer.getOwnerName();
    User owner = userService.findByUserId(ownerName);
    if (owner == null) {
      throw new BizException(String.format("User does not exist. UserId = %s", ownerName));
    }
    consumer.setOwnerEmail(owner.getEmail());

    return consumerRepository.save(consumer);
  }

  public ConsumerToken generateAndSaveConsumerToken(Consumer consumer, Date expires) {
    Preconditions.checkArgument(consumer != null, "Consumer can not be null");

    ConsumerToken consumerToken = generateConsumerToken(consumer, expires);
    consumerToken.setId(0L);

    return consumerTokenRepository.save(consumerToken);
  }

  public ConsumerToken getConsumerTokenByAppId(String appId) {
    Consumer consumer = consumerRepository.findByAppId(appId);
    if (consumer == null) {
      return null;
    }

    return consumerTokenRepository.findByConsumerId(consumer.getId());
  }

  public Long getConsumerIdByToken(String token) {
    if (Strings.isNullOrEmpty(token)) {
      return null;
    }
    ConsumerToken consumerToken = consumerTokenRepository.findTopByTokenAndExpiresAfter(token,
                                                                                        new Date());
    return consumerToken == null ? null : consumerToken.getConsumerId();
  }

  public Consumer getConsumerByConsumerId(long consumerId) {
    return consumerRepository.findById(consumerId).orElse(null);
  }
//
//  public List<ConsumerRole> assignNamespaceRoleToConsumer(String token, String appId, String namespaceName) {
//    return assignNamespaceRoleToConsumer(token, appId, namespaceName, null);
//  }

//  @Transactional
//  public List<ConsumerRole> assignNamespaceRoleToConsumer(String token, String appId, String namespaceName, String env) {
//    Long consumerId = getConsumerIdByToken(token);
//    if (consumerId == null) {
//      throw new BizException("Token is Illegal");
//    }
//
//    Role namespaceModifyRole =
//        rolePermissionService.findRoleByRoleName(RoleUtils.buildModifyNamespaceRoleName(appId, namespaceName, env));
//    Role namespaceReleaseRole =
//        rolePermissionService.findRoleByRoleName(RoleUtils.buildReleaseNamespaceRoleName(appId, namespaceName, env));
//
//    if (namespaceModifyRole == null || namespaceReleaseRole == null) {
//      throw new BizException("Namespace's role does not exist. Please check whether namespace has created.");
//    }
//
//    long namespaceModifyRoleId = namespaceModifyRole.getId();
//    long namespaceReleaseRoleId = namespaceReleaseRole.getId();
//
//    ConsumerRole managedModifyRole = consumerRoleRepository.findByConsumerIdAndRoleId(consumerId, namespaceModifyRoleId);
//    ConsumerRole managedReleaseRole = consumerRoleRepository.findByConsumerIdAndRoleId(consumerId, namespaceReleaseRoleId);
//    if (managedModifyRole != null && managedReleaseRole != null) {
//      return Arrays.asList(managedModifyRole, managedReleaseRole);
//    }
//
//    String operator = userInfoHolder.getUser().getUserId();
//
//    ConsumerRole namespaceModifyConsumerRole = createConsumerRole(consumerId, namespaceModifyRoleId, operator);
//    ConsumerRole namespaceReleaseConsumerRole = createConsumerRole(consumerId, namespaceReleaseRoleId, operator);
//
//    ConsumerRole createdModifyConsumerRole = consumerRoleRepository.save(namespaceModifyConsumerRole);
//    ConsumerRole createdReleaseConsumerRole = consumerRoleRepository.save(namespaceReleaseConsumerRole);
//
//    return Arrays.asList(createdModifyConsumerRole, createdReleaseConsumerRole);
//  }

//  @Transactional
//  public ConsumerRole assignAppRoleToConsumer(String token, String appId) {
//    Long consumerId = getConsumerIdByToken(token);
//    if (consumerId == null) {
//      throw new BizException("Token is Illegal");
//    }
//
//    Role masterRole = rolePermissionService.findRoleByRoleName(RoleUtils.buildAppMasterRoleName(appId));
//    if (masterRole == null) {
//      throw new BizException("App's role does not exist. Please check whether app has created.");
//    }
//
//    long roleId = masterRole.getId();
//    ConsumerRole managedModifyRole = consumerRoleRepository.findByConsumerIdAndRoleId(consumerId, roleId);
//    if (managedModifyRole != null) {
//      return managedModifyRole;
//    }
//
//    String operator = userInfoHolder.getUser().getUserId();
//    ConsumerRole consumerRole = createConsumerRole(consumerId, roleId, operator);
//    return consumerRoleRepository.save(consumerRole);
//  }

  @Transactional
  public void createConsumerAudits(Iterable<ConsumerAudit> consumerAudits) {
    consumerAuditRepository.saveAll(consumerAudits);
  }

  @Transactional
  public ConsumerToken createConsumerToken(ConsumerToken entity) {
    entity.setId(0L); //for protection

    return consumerTokenRepository.save(entity);
  }

  private ConsumerToken generateConsumerToken(Consumer consumer, Date expires) {
    long consumerId = consumer.getId();
    String createdBy = YyRequestInfoHelper.getCurrentUserRealName();
    Date createdTime = new Date();

    ConsumerToken consumerToken = new ConsumerToken();
    consumerToken.setConsumerId(consumerId);
    consumerToken.setExpires(expires);
//    consumerToken.setDataChangeCreatedBy(createdBy);
//    consumerToken.setDataChangeCreatedTime(createdTime);
//    consumerToken.setDataChangeLastModifiedBy(createdBy);
//    consumerToken.setDataChangeLastModifiedTime(createdTime);

    generateAndEnrichToken(consumer, consumerToken);

    return consumerToken;
  }

  void generateAndEnrichToken(Consumer consumer, ConsumerToken consumerToken) {

    Preconditions.checkArgument(consumer != null);

//    if (consumerToken.getCreateTime() == null) {
//      consumerToken.setDataChangeCreatedTime(new Date());
//    }
    consumerToken.setToken(generateToken(consumer.getAppId(), new Date(), portalConfig.consumerTokenSalt()));
  }

  String generateToken(String consumerAppId, Date generationTime, String
      consumerTokenSalt) {
    return Hashing.sha1().hashString(KEY_JOINER.join(consumerAppId, TIMESTAMP_FORMAT.format
        (generationTime), consumerTokenSalt), Charsets.UTF_8).toString();
  }
//
//    ConsumerRole createConsumerRole(Long consumerId, Long roleId, String operator) {
//    ConsumerRole consumerRole = new ConsumerRole();
//
//    consumerRole.setConsumerId(consumerId);
//    consumerRole.setRoleId(roleId);
//    consumerRole.setDataChangeCreatedBy(operator);
//    consumerRole.setDataChangeLastModifiedBy(operator);
//
//    return consumerRole;
//  }
//
  public Set<String> findAppIdsAuthorizedByConsumerId(long consumerId) {
//    List<ConsumerRole> consumerRoles = this.findConsumerRolesByConsumerId(consumerId);
//    List<Long> roleIds = consumerRoles.stream().map(ConsumerRole::getRoleId)
//        .collect(Collectors.toList());
    Set<String> collect = appRepository.findAll().stream().map(App::getAppCode).collect(Collectors.toSet());
    return collect;
  }
//
//  private List<ConsumerRole> findConsumerRolesByConsumerId(long consumerId) {
//    return this.consumerRoleRepository.findByConsumerId(consumerId);
//  }
//
//  private Set<String> findAppIdsByRoleIds(List<Long> roleIds) {
//    Iterable<Role> roleIterable = this.appRepository.findAllById(roleIds);
//
//    Set<String> appIds = new HashSet<>();
//
//    roleIterable.forEach(role -> {
//      if (!role.isDeleted()) {
//        String roleName = role.getRoleName();
//        String appId = RoleUtils.extractAppIdFromRoleName(roleName);
//        appIds.add(appId);
//      }
//    });
//
//    return appIds;
//  }
}

/*
 *    Copyright 2018-2019 the original author or authors.
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
package com.yofish.gary.biz.service.impl;

import com.google.common.collect.Sets;
import com.yofish.gary.api.dto.req.RoleAddReqDTO;
import com.yofish.gary.api.dto.req.RoleDeleteReqDTO;
import com.yofish.gary.api.dto.req.RoleEditReqDTO;
import com.yofish.gary.api.dto.req.RoleQueryReqDTO;
import com.yofish.gary.api.dto.rsp.RoleQueryRspDTO;
import com.yofish.gary.biz.domain.Permission;
import com.yofish.gary.biz.domain.Role;
import com.yofish.gary.biz.repository.PermissionRepository;
import com.yofish.gary.biz.repository.RoleRepository;
import com.yofish.gary.biz.service.RoleService;
import com.youyu.common.api.PageData;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.yofish.gary.api.enums.UpmsResultCode.ROLE_NAME_ALREADY_EXIST;
import static com.yofish.gary.utils.OrikaCopyUtil.copyProperty4List;
import static java.util.Objects.nonNull;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 角色service impl
 */
@Service
public class RoleServiceImpl implements RoleService {


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(RoleAddReqDTO roleAddReqDTO) {
        checkRoleName(roleAddReqDTO.getRoleName());
        Role role = new Role(roleAddReqDTO);

        Set<Permission> permissions = Sets.newHashSet(permissionRepository.findAllById(roleAddReqDTO.getPermissionIds()));
        role.setPermissions(permissions);
        roleRepository.save(role);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(RoleDeleteReqDTO roleDeleteReqDTO) {
        Long roleId = roleDeleteReqDTO.getRoleId();
        roleRepository.deleteById(roleId); //同时删除关系
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(RoleEditReqDTO roleEditReqDTO) {
        Role role = new Role(roleEditReqDTO);
        Set<Permission> permissions = Sets.newHashSet(permissionRepository.findAllById(roleEditReqDTO.getPermissionIds()));
        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    @Override
    public PageData<RoleQueryRspDTO> getPage(RoleQueryReqDTO roleQueryReqDTO) {
//        startPage(roleQueryReqDTO.getPageNo(), roleQueryReqDTO.getPageSize());
////        List<Role> roles = roleMapper.getPage(roleQueryReqDTO);
//        List<Role> roles = null;
//        PageInfo<Role> rolePage = new PageInfo<>(roles);
//
//        List<RoleQueryRspDTO> roleQueryRsps = copyProperty4List(rolePage.getList(), RoleQueryRspDTO.class);
////        fillRolePermission(roleQueryRsps);
//        return pageInfo2PageData(rolePage, roleQueryRsps);
        return null;
    }

    @Override
    public Role findByRoleName(String roleName) {
        return this.roleRepository.findRoleByRoleName(roleName);
    }

    /**
     * 检查角色名是否已经存在
     *
     * @param roleName
     */
    private void checkRoleName(String roleName) {
        Role role = roleRepository.findRoleByRoleName(roleName);
        if (nonNull(role)) {
            throw new BizException(ROLE_NAME_ALREADY_EXIST);
        }
    }

    /**
     * 更新角色权限信息
     *
     * @param role
     * @param permissionIds
     */
    private void updateRolePermissions(Role role, List<Long> permissionIds) {
//        roleRepository.deleteByRoleId(role.getId());
//        saveRolePermissions(role, permissionIds);
    }

}

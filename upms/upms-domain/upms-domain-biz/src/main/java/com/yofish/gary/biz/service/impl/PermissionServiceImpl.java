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
import com.yofish.gary.api.dto.req.*;
import com.yofish.gary.api.dto.rsp.PermissionQueryRspDTO;
import com.yofish.gary.api.dto.rsp.PermissionTreeRspDTO;
import com.yofish.gary.biz.domain.Permission;
import com.yofish.gary.biz.domain.Role;
import com.yofish.gary.biz.helper.constant.PermissionConstant;
import com.yofish.gary.biz.repository.PermissionRepository;
import com.yofish.gary.biz.repository.PermissionRepository4Menu;
import com.yofish.gary.biz.repository.RoleRepository;
import com.yofish.gary.biz.service.PermissionService;
import com.youyu.common.api.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.yofish.gary.utils.OrikaCopyUtil.copyProperty4List;


/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限service impl
 */
@Service
public class PermissionServiceImpl implements PermissionService {


    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    PermissionRepository4Menu permissionRepository4Menu;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PermissionAddReqDTO permissionAddReqDTO) {

        Permission permission = new Permission(permissionAddReqDTO);

        Set<Role> roles = Sets.newHashSet(roleRepository.findAllById(permissionAddReqDTO.getRoleIds()));
        permission.setRoles(roles);
        permissionRepository.save(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(PermissionDeleteReqDTO permissionDeleteReqDTO) {
        Long permissionId = permissionDeleteReqDTO.getPermissionId();
        permissionRepository.deleteById(permissionId);
//        permissionMapper.deleteByPrimaryKey(permissionId);
//        rolePermissionMapper.deleteByPermissionId(permissionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(PermissionEditReqDTO permissionEditReqDTO) {
        Permission permission = new Permission(permissionEditReqDTO);
//        permissionMapper.updateByPrimaryKeySelective(permission);
        permissionRepository.save(permission);
    }

    @Override
    public PageData<PermissionQueryRspDTO> getPage(PermissionQueryReqDTO permissionQueryReqDTO) {
//        startPage(permissionQueryReqDTO.getPageNo(), permissionQueryReqDTO.getPageSize());
//        List<Permission> permissions = permissionMapper.getPage(permissionQueryReqDTO);
        Pageable pageable = Pageable.unpaged();
        //TODO fix
//        List<Permission> permissions = permissionRepository.findAllByPermissionIdAndPermissionName(permissionQueryReqDTO.getPermissionId(),permissionQueryReqDTO.getPermissionName(),pageable);
//        PageInfo<Permission> permissionPage = new PageInfo<>(permissions);
//
//        List<PermissionQueryRspDTO> permissionQueryRsps = copyProperty4List(permissions, PermissionQueryRspDTO.class);
//        return pageInfo2PageData(permissionPage, permissionQueryRsps);
        return null;
    }

    @Override
    public boolean isUrlExist(String url) {
        long count = permissionRepository4Menu.countByIframeUrl(url);
        return count == 0 ? false : true;
    }

    @Override
    public List<PermissionTreeRspDTO> getPermissionTree(PermissionTreeReqDTO permissionTreeReqDTO) {
        List<Permission> rootPermissions = permissionRepository.findPermissionsByParentId(PermissionConstant.ROOT_PERMISSION);
        return copyProperty4List(rootPermissions, PermissionTreeRspDTO.class);
    }

}

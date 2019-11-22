package com.yofish.gary.biz.repository;

import com.yofish.gary.biz.domain.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


public interface PermissionRepository extends PagingAndSortingRepository<Permission, Long> {



    List<Permission> findPermissionsByParentId(Long parentId);

//    List<Permission> findAllByPermissionIdAndPermissionName(Long permissoionId, String permissionNam, Pageable pageable);

}

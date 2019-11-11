package com.yofish.gary.biz.repository;

import com.yofish.gary.biz.domain.Permission;
import com.yofish.gary.biz.domain.Permission4Menu;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/11 下午2:56
 */
public interface PermissionRepository4Menu extends PagingAndSortingRepository<Permission4Menu, Long> {


    long countByIframeUrl(String url);
}

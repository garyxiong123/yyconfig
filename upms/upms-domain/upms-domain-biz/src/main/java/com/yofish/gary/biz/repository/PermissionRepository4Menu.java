/*
 *    Copyright 2019-2020 the original author or authors.
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

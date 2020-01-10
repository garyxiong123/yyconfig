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

import com.yofish.gary.biz.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String userName);

    User findUserByUsernameAndPassword(String username, String password);

    User findUserByUsernameAndEmail(String username, String email);

    User findUserByEmail(String email);

    //    Page<User> findAllByIdAndStatusAndUsernameOrRealNameOrPhoneOrEmail(Long id, String status, String username, String realName, String phone, String email, Pageable pageable);
    Page<User> findAllByUsernameContainingOrRealNameContainingOrEmailContaining(String username, String realName, String email, Pageable pageable);
}

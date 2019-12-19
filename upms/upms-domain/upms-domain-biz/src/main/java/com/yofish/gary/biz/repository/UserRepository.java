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

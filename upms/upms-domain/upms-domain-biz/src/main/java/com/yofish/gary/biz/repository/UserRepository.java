package com.yofish.gary.biz.repository;

import com.yofish.gary.biz.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String userName);

    User findUserByUsernameAndPassword(String username, String password);

    User findUserByUsernameAndEmail(String username, String email);

    User findUserByEmail(String email);
}

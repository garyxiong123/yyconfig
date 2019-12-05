package com.yofish.gary.biz.config;

import com.yofish.gary.api.dto.req.UserAddReqDTO;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.UserRepository;
import com.yofish.gary.biz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * @author WangSongJun
 * @date 2019-12-05
 */
@Slf4j
@Component
@DependsOn("strategyNumBean")
public class DataImport {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private String adminUsername = "apollo";
    private String adminPassword = "apollo";
    private String adminRealName = "管理员用户";

    @PostConstruct
    public void importUser() {
        log.info("初始化用户数据...");

        User defaultAdminUser = this.userRepository.findByUsername(adminUsername);

        if (isEmpty(defaultAdminUser)) {
            UserAddReqDTO admin = createAdmin();
            Long userId = this.userService.add(admin);

            User user = userRepository.findById(userId).orElse(null);
            log.info("初始用户成功！\n UserName:{},Password:{}", user.getUsername(), user.getPassword());
        } else {
            log.info("用户已存在！\n UserName:{},Password:{}", defaultAdminUser.getUsername(), defaultAdminUser.getPassword());
        }
    }

    private UserAddReqDTO createAdmin() {
        return UserAddReqDTO.builder().username(adminUsername).password(adminPassword).realName(adminRealName).remark("初始用户").build();
    }
}

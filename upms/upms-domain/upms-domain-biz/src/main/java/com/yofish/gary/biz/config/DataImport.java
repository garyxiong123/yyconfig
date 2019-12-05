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

    @PostConstruct
    public void importUser() {
        log.info("初始化用户数据...");

        User defaultUser = this.userRepository.findByUsername("apollo");

        if (ObjectUtils.isEmpty(defaultUser)) {
            Long userId = this.userService.add(UserAddReqDTO.builder().username("apollo").password("apollo").realName("Apollo").remark("初始用户").build());

            User user = userRepository.findById(userId).orElse(null);
            log.info("初始用户成功！\nUserName:{},Password:{}", user.getUsername(), user.getPassword());
        } else {
            log.info("用户已存在！\nUserName:{},Password:{}", defaultUser.getUsername(), defaultUser.getPassword());
        }
    }
}

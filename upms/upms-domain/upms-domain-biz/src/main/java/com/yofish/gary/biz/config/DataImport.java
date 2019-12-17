package com.yofish.gary.biz.config;

import com.yofish.gary.api.dto.req.UserAddReqDTO;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.UserRepository;
import com.yofish.gary.biz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * @author WangSongJun
 * @date 2019-12-05
 */
@Slf4j
@Component("userDataImport")
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

        if (!isEmpty(defaultAdminUser)) {
            log.info("用户已存在！\n UserName:{},Password:{}", defaultAdminUser.getUsername(), defaultAdminUser.getPassword());
            return;
        }

        UserAddReqDTO initAdmin = createInitAdmin();
        Long initAdminId = this.userService.add(initAdmin);
        User initAdminUser = userRepository.findById(initAdminId).orElse(null);
        log.info("初始用户成功！\n UserName:{},Password:{}", initAdminUser.getUsername(), initAdminUser.getPassword());

    }


    private UserAddReqDTO createInitAdmin() {
        List<Long> initRoles = createInitRoles();
        UserAddReqDTO userAddReqDTO = UserAddReqDTO.builder().username(adminUsername).password(adminPassword).roleIds(initRoles).realName(adminRealName).remark("初始用户").build();
        return userAddReqDTO;
    }

    private List<Long> createInitRoles() {
        return null;
    }
}

package com.yofish.apollo.pattern.strategy;

import com.yofish.apollo.domain.App;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.repository.DepartmentRepository;
import com.yofish.gary.biz.service.UserService;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Set;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/7/26 下午10:40
 */
@Component
public class CheckOwnerAndAdminsAndDepartmentStrategy {
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentRepository departmentRepository;

    public void check(App app) {
        //owner
        com.yofish.gary.api.dto.rsp.UserDetailRspDTO userDetail = userService.getUserDetail(app.getAppOwner().getId());
        if (userDetail == null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Application's owner not exist.");
        }

        //admins
        this.checkAppAdminsIsExist(app.getAppAdmins());

        //department
        Department department = departmentRepository.findById(app.getDepartment().getId()).orElse(null);
        if (department == null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Application's department not exist.");
        }
    }

    private void checkAppAdminsIsExist(Set<User> appAdmins) {
        if (!ObjectUtils.isEmpty(appAdmins)) {
            for (User appAdmin : appAdmins) {
                com.yofish.gary.api.dto.rsp.UserDetailRspDTO userDetailRspDTO = userService.getUserDetail(appAdmin.getId());
                if (userDetailRspDTO == null) {
                    throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Application's admin [" + appAdmin.getId() + "] not exist.");
                }
            }
        }
    }
}

package com.yofish.apollo.config;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Department;
import com.yofish.apollo.service.AppService;
import com.yofish.apollo.service.DepartmentService;
import com.yofish.gary.api.feign.UserApi;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.youyu.common.helper.YyRequestInfoHelper;
import com.youyu.common.utils.YyAssert;
import common.condition.PermissionAuth;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * created by zhangyingbin on 2019/12/18 0018 上午 11:00
 * description:
 */
@Slf4j
@Aspect
public class PremissionAuthAdvice {

    @Autowired
    private UserApi userApi;

    @Autowired
    private AppService appService;

    @Autowired
    private DepartmentService departmentService;

    @Before("@annotation(common.condition.PermissionAuth)")
    public void validate(JoinPoint joinPoint) {
        //当前用户ID
        Long currentUserId = YyRequestInfoHelper.getCurrentUserId();
        YyAssert.isTrue(!ObjectUtils.isEmpty(currentUserId), "403", "用户未登录！");
        log.info("portal 数据权限验证 userId:[{}]", currentUserId);
        if (userApi.isAdmin(currentUserId).ifNotSuccessThrowException().getData()) {
            log.info("用户是管理员！");
            return;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("进行 @Premission 权限验证：[{}]", request.getRequestURL());
        //目标节点的ID
        Long appId = ((Map<String, Long>) request.getAttribute(View.PATH_VARIABLES)).get("appId");

        YyAssert.paramCheck(ObjectUtils.isEmpty(appId), "pathVariables 没有 appId！");

        // 获取目标接口的权限验证类型
        Class targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        PermissionAuth.PermissionType permissionType = this.getPermissionType(targetClass, methodName, parameterTypes);
        log.info("permissionType:[{}]", permissionType);

        //进行权限验证
        if (permissionType.equals(PermissionAuth.PermissionType.leader)) {
            log.info("负责人权限验证--> appId:[{}]", appId);
            App app = appService.getApp(appId);
            YyAssert.paramCheck(ObjectUtils.isEmpty(app), "应用ID[" + appId + "]不存在！");
            boolean appPermission = false;

            if (currentUserId.equals(app.getAppOwner().getId())) {
                appPermission = true;
            }
            YyAssert.isTrue(appPermission, "当前用户没有项目[" + appId + "]的[" + permissionType + "]权限。");

        } else if (permissionType.equals(PermissionAuth.PermissionType.participant)) {
            log.info("参与人权限验证--> appId:[{}]", appId);
            App app = appService.getApp(appId);
            YyAssert.paramCheck(ObjectUtils.isEmpty(app), "应用ID[" + appId + "]不存在！");
            boolean participantPermission = false;
            List<Long> userIds = app.getAppAdmins().stream().map(e -> e.getId()).collect(Collectors.toList());
            if (userIds.contains(currentUserId)) {
                participantPermission = true;
            }
            YyAssert.isTrue(participantPermission, "当前用户没有项目[" + appId + "]的[" + permissionType + "]权限。");
        } else {
            log.info("普通用户权限验证--> appId:[{}]", appId);
            App app = appService.getApp(appId);
            YyAssert.paramCheck(ObjectUtils.isEmpty(app), "应用ID[" + appId + "]不存在！");
            Department department = departmentService.findOneById(app.getDepartment().getId());
            List<Long> userIds = department.getUsers().stream().map(e -> e.getId()).collect(Collectors.toList());
            boolean userPermission = false;
            if (userIds.contains(currentUserId)) {
                userPermission = true;
            }
            YyAssert.isTrue(userPermission, "当前用户没有项目[" + appId + "]的[" + permissionType + "]权限。");
        }
        log.info("@PermissionAuth 权限验证验证通过！");
    }

    /**
     * @create: zhangyingbin 2019/12/18 0018 下午 2:43
     * @Modifier:
     * @Description: 获取方法上 PermissionAuth 注解中的 PermissionType (需要认证的权限类型)
     */
    private PermissionAuth.PermissionType getPermissionType(Class targetClass, String methodName, Class<?>[] parameterTypes) {

        try {
            Method method = targetClass.getMethod(methodName, parameterTypes);
            PermissionAuth.PermissionType permissionType = method.getAnnotation(PermissionAuth.class).value();
            return permissionType;
        } catch (NoSuchMethodException e) {
            throw new BizException(BaseResultCode.SYSTEM_ERROR, e);
        }
    }

}

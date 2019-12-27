package com.yofish.apollo.config;

import com.yofish.apollo.component.AppPreAuthorize;
import com.yofish.apollo.component.PermissionValidator;
import com.yofish.gary.api.enums.UpmsResultCode;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.youyu.common.helper.YyRequestInfoHelper;
import com.youyu.common.utils.YyAssert;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * 项目相关操作的权限验证
 *
 * @author WangSongJun
 * @date 2019-12-25
 */
@Slf4j
@Aspect
@Component
public class AppPreAuthorizeHandler {
    @Autowired
    private PermissionValidator permissionValidator;


    /**
     * 按照权限级别逐层判断放行
     *
     * @param joinPoint
     */
    @Before("@annotation(com.yofish.apollo.component.AppPreAuthorize)")
    public void validate(JoinPoint joinPoint) {
        log.info("进行项目相关操作的权限验证...");

        //当前用户ID
        Long currentUserId = YyRequestInfoHelper.getCurrentUserId();
        YyAssert.assertTrue(isEmpty(currentUserId), UpmsResultCode.USER_SESSION_EXPIRED);

        //超级管理员直接放行
        if (permissionValidator.isSuperAdmin()) {
            log.info("用户是管理员，直接放行！");
            return;
        }

        // 获取目标接口的权限验证类型
        Class targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();

        AppPreAuthorize.Authorize requireAuthorizeType = this.getAuthorizeType(targetClass, methodName, parameterTypes);
        log.info("目标接口的权限验证类型:[{}]", requireAuthorizeType);

        if (requireAuthorizeType.equals(AppPreAuthorize.Authorize.SuperAdmin)) {
            log.info("要求超级管理员权限，当前用户没有超级管理员权限！");
            throw new BizException("403", "当前用户没有超级管理员权限。");
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("进行项目权限验证：[{}]", request.getRequestURL());

        //项目的ID或Code标识
        Long appId = ((Map<String, Long>) request.getAttribute(View.PATH_VARIABLES)).get("appId");
        String appCode = ((Map<String, String>) request.getAttribute(View.PATH_VARIABLES)).get("appCode");
        YyAssert.paramCheck(isEmpty(appId) && isEmpty(appCode), "pathVariables 没有 appId 或 appCode！");

        //项目拥有者放行
        boolean isAppOwner = isEmpty(appId) ? permissionValidator.isAppOwner(appCode) : permissionValidator.isAppOwner(appId);
        if (isAppOwner) {
            log.info("项目拥有者放行.");
            return;
        } else if (AppPreAuthorize.Authorize.AppOwner.equals(requireAuthorizeType)) {
            throw new BizException("403", "当前用户没有项目[" + (isEmpty(appId) ? appCode : appId) + "]的[" + requireAuthorizeType + "]权限。");
        }

        //项目参与人放行
        boolean isAppAdmin = isEmpty(appId) ? permissionValidator.isAppAdmin(appCode) : permissionValidator.isAppAdmin(appId);
        if (isAppAdmin) {
            log.info("项目参与人放行.");
            return;
        } else if (AppPreAuthorize.Authorize.AppAdmin.equals(requireAuthorizeType)) {
            throw new BizException("403", "当前用户没有项目[" + (isEmpty(appId) ? appCode : appId) + "]的[" + requireAuthorizeType + "]权限。");
        }

        //同项目部门普通用户放行
        boolean isSameDepartment = isEmpty(appId) ? permissionValidator.isSameDepartment(appCode) : permissionValidator.isSameDepartment(appId);
        if (isSameDepartment) {
            log.info("同项目部门普通用户放行.");
            return;
        } else {
            throw new BizException("403", "当前用户没有项目[" + (isEmpty(appId) ? appCode : appId) + "]的[" + requireAuthorizeType + "]权限。");
        }
    }


    private AppPreAuthorize.Authorize getAuthorizeType(Class targetClass, String methodName, Class<?>[] parameterTypes) {
        try {
            Method method = targetClass.getMethod(methodName, parameterTypes);
            AppPreAuthorize.Authorize authorizeType = method.getAnnotation(AppPreAuthorize.class).value();
            return authorizeType;
        } catch (NoSuchMethodException e) {
            throw new BizException(BaseResultCode.SYSTEM_ERROR, e);
        }
    }

}

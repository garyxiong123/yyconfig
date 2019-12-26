package com.yofish.apollo.config;

import com.yofish.apollo.component.AppPreAuthorize;
import com.yofish.apollo.component.PermissionValidator;
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


    @Before("@annotation(com.yofish.apollo.component.AppPreAuthorize)")
    public void validate(JoinPoint joinPoint) {
        //TODO 上线待删除
        if (true) {
            log.info("====================   测试阶段没有用户登录，暂不做验证！！       ====================");
            return;
        }

        //当前用户ID
        Long currentUserId = YyRequestInfoHelper.getCurrentUserId();
        YyAssert.isTrue(!ObjectUtils.isEmpty(currentUserId), "403", "用户未登录！");
        log.info("portal 数据权限验证 userId:[{}]", currentUserId);
        if (permissionValidator.isSuperAdmin()) {
            log.info("用户是管理员！");
            return;
        }

        // 获取目标接口的权限验证类型
        Class targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();

        AppPreAuthorize.Authorize authorizeType = this.getAuthorizeType(targetClass, methodName, parameterTypes);
        log.info("authorizeType:[{}]", authorizeType);

        if (authorizeType.equals(AppPreAuthorize.Authorize.SuperAdmin)) {
            log.info("要求超级管理员权限！");
            throw new BizException("403", "当前用户没有超级管理员权限。");
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("进行权限验证：[{}]", request.getRequestURL());

        //项目的ID标识
        long appId = ((Map<String, Long>) request.getAttribute(View.PATH_VARIABLES)).get("appId");
        String appCode = ((Map<String, String>) request.getAttribute(View.PATH_VARIABLES)).get("appCode");

        YyAssert.paramCheck(ObjectUtils.isEmpty(appId) && ObjectUtils.isEmpty(appCode), "pathVariables 没有 appId 或 appCode！");

        //进行权限验证
        if (authorizeType.equals(AppPreAuthorize.Authorize.AppAdmin)) {
            log.info("项目负责人权限验证...");
            boolean authorize = ObjectUtils.isEmpty(appId) ? permissionValidator.isAppOwner(appCode):permissionValidator.isAppOwner(appId);
            YyAssert.isTrue(authorize, "当前用户没有项目[" + (ObjectUtils.isEmpty(appId) ?appCode : appId) + "]的[" + authorizeType + "]权限。");

        } else if (authorizeType.equals(AppPreAuthorize.Authorize.AppAdmin)) {
            log.info("参与人权限验证...");
            boolean authorize = ObjectUtils.isEmpty(appId) ? permissionValidator.isAppAdmin(appCode):permissionValidator.isAppAdmin(appId);

            YyAssert.isTrue(authorize, "当前用户没有项目[" + (ObjectUtils.isEmpty(appId) ?appCode : appId) + "]的[" + authorizeType + "]权限。");
        } else {
            log.info("同项目部门普通用户权限验证...");
            // TODO: 2019-12-26 部门验证
//            boolean authorize = ObjectUtils.isEmpty(appId) ? permissionValidator.isAppAdmin(appCode):permissionValidator.isAppAdmin(appId);

//            YyAssert.isTrue(authorize, "当前用户没有项目[" + appId + "]的[" + authorizeType + "]权限。");
//            YyAssert.isTrue(userPermission, "当前用户没有项目[" + appId + "]的[" + authorizeType + "]权限。");
        }
        log.info("@PermissionAuth 权限验证验证通过！");
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

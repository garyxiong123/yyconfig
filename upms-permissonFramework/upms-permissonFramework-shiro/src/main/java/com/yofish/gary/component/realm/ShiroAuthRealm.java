/*
 *    Copyright 2018-2019 the original author or authors.
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
package com.yofish.gary.component.realm;

import com.yofish.gary.api.properties.ShiroProperties;
import com.yofish.gary.api.login.UpmsLoginLogoutRealm;
import com.yofish.gary.component.strategy.shiro.session.ShiroConcurrentSessionStrategy;
import com.yofish.gary.api.ShiroSimpleHashStrategy;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.biz.service.UserService;
import com.yofish.gary.tuple.Tuple2;
import com.yofish.gary.api.dto.req.UserLoginReqDTO;
import com.yofish.gary.api.dto.rsp.UserLoginRspDTO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;
import static com.yofish.gary.utils.BizExceptionUtil.exception2MatchingExpression;
import static com.yofish.gary.utils.OrikaCopyUtil.copyProperty;
import static com.yofish.gary.api.enums.UpmsResultCode.SESSION_USER_ID_IS_NULL;
import static com.yofish.gary.api.enums.UpmsResultCode.USER_SESSION_EXPIRED;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro认证授权Realm
 */
public class ShiroAuthRealm extends AuthorizingRealm implements UpmsLoginLogoutRealm {

    @Autowired
    private ShiroProperties shiroProperties;

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户的id
     *
     * @return
     */
    public static Long getUserId() {
        Subject subject = SecurityUtils.getSubject();
        exception2MatchingExpression(!subject.isAuthenticated(), USER_SESSION_EXPIRED);

        Session session = subject.getSession();
        Long userId = (Long) session.getAttribute(session.getId());
        exception2MatchingExpression(isNull(userId), SESSION_USER_ID_IS_NULL);
        return userId;
    }

    @Override
    public UserLoginRspDTO login(UserLoginReqDTO userLoginReqDTO) {
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userLoginReqDTO.getUsername(), userLoginReqDTO.getPassword());
        Subject subject = SecurityUtils.getSubject();
        subject.login(usernamePasswordToken);

        ShiroConcurrentSessionStrategy shiroConcurrentSessionStrategy = getBeanInstance(ShiroConcurrentSessionStrategy.class, shiroProperties.getConcurrentSessionStrategyNumber());
        shiroConcurrentSessionStrategy.handleConcurrentSession();

        User user = (User) subject.getPrincipal();
        return copyProperty(user, UserLoginRspDTO.class);
    }

    @Override
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        User user = (User) principalCollection.getPrimaryPrincipal();
        if (isNull(user)) {
            return null;
        }

        Tuple2<Set<String>, Set<String>> rolePermissionTuple2 = userService.getRolePermissionTuple2(user.getId());
        simpleAuthorizationInfo.setRoles(rolePermissionTuple2.a);
        simpleAuthorizationInfo.setStringPermissions(rolePermissionTuple2.b);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String username = (String) authenticationToken.getPrincipal();
        String password = getPasswordBySimpleHash(usernamePasswordToken);

        User user = userService.getAuthenticationUser(username, password);
        return new SimpleAuthenticationInfo(user, password, ByteSource.Util.bytes(shiroProperties.getSalt()), getName());
    }

    /**
     * 获取算法处理后的密码
     *
     * @param usernamePasswordToken
     * @return
     */
    private String getPasswordBySimpleHash(UsernamePasswordToken usernamePasswordToken) {
        String password = new String(usernamePasswordToken.getPassword());
        ShiroSimpleHashStrategy shiroSimpleHashStrategy = getBeanInstance(ShiroSimpleHashStrategy.class, defaultIfBlank(shiroProperties.getHashAlgorithmName(), "sha-256)"));
        String signaturePassword = shiroSimpleHashStrategy.signature(password);
        return signaturePassword;
    }

}

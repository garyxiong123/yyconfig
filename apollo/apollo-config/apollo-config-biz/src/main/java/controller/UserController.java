package controller;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.youyu.common.api.Result;
import com.youyu.common.constant.ApplicationInfo;
import com.youyu.platform.sample.provider.api.UserApi;
import com.youyu.platform.sample.provider.dto.UserInfoDto;
import com.youyu.platform.sample.provider.param.UserInfoParam;
import com.youyu.platform.sample.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiongchengwei
 * @date 2018/8/20
 */


/**
 * api 层不定义
 */
@Slf4j
@Api("告警接口")
@RestController
@EnableApolloConfig
public class UserController implements UserApi, ApplicationContextAware {
    @Autowired
    private HttpServletRequest httpServletRequest;

    private ApplicationContext applicationContext;

    @Autowired
    private UserService userService;
    @Value("${test.name:gary}")
    public String name1;


    @ApolloConfig("application")
    private Config config;

    @ApolloConfig("testJson.json")
    private Config testJsonConfig;



// @Value("${request.timeout:200}")
//    private int timeout;

    @ApolloConfigChangeListener("application")
    private void someOnChange(ConfigChangeEvent changeEvent) {
        changeEvent.changedKeys().forEach(key -> {
            ConfigChange change = changeEvent.getChange(key);
            System.out.println(String.format("Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s", change.getPropertyName(), change.getOldValue(), change.getNewValue(), change.getChangeType()));
        });
    }


    @Override
    @PostMapping("/getUserInfo")
    public Result<UserInfoDto> getUserInfo(@RequestBody UserInfoParam userInfoParam) {
//        userService.selectAll()
//        Transaction t = Cat.getProducer().newTransaction("Exec", "CaiYiMessage");
//        try {
        System.out.println(ApplicationInfo.getApplicationNameFromHttpContext(httpServletRequest));
//        } catch (Exception e) {
//            t.setStatus(e);
//            throw new BizException("不存在的短信签名", e);
//        } finally {
//            t.complete();
//        }
        return Result.ok(userService.getUserInfo(userInfoParam.getName()));
    }


    @GetMapping("/getUserInfo1")
    public Result<String> getUserInfo1(String name, int age) {

        System.out.println(name1);

        System.out.println(config);

        System.out.println(testJsonConfig);

////        userService.selectAll()
//        Transaction t = Cat.getProducer().newTransaction("Exec", "CaiYiMessage");
//        try {
//            System.out.println(ApplicationInfo.getApplicationNameFromHttpContext(httpServletRequest));
//        } catch (Exception e) {
//            t.setStatus(e);
//            throw new BizException("不存在的短信签名", e);
//        } finally {
//            t.complete();
//        }
        return Result.ok(name1);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}

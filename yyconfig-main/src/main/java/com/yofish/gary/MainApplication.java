package com.yofish.gary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FlashMapManager;

import static org.springframework.web.servlet.DispatcherServlet.FLASH_MAP_MANAGER_BEAN_NAME;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */

@ComponentScan(basePackages = {"com.yofish.gary", "com.yofish.apollo", "com.yofish.apollo.repository", "com.ctrip.framework.apollo.configservice"})
@EnableJpaRepositories(basePackages = {"com.yofish.apollo.repository", "com.yofish.gary.biz.repository"})
@EnableJpaAuditing
@EntityScan(basePackages = {"com.yofish.apollo.domain", "com.yofish.gary"})
@Configuration
@SpringBootApplication
public class MainApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);
        FlashMapManager flashMapManager = (FlashMapManager) run.getBean(FLASH_MAP_MANAGER_BEAN_NAME, FlashMapManager.class);
        flashMapManager = null;

        System.out.println(run);

    }

}

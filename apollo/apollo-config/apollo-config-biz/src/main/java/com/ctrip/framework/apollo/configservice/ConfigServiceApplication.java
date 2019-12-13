package com.ctrip.framework.apollo.configservice;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring boot application entry point
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@EnableAspectJAutoProxy
@EnableAutoConfiguration
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackageClasses = {ApolloCommonConfig.class, ApolloBizConfig.class, ConfigServiceApplication.class})
public class ConfigServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(ConfigServiceApplication.class).run(args);
        context.addApplicationListener(new ApplicationPidFileWriter());
//        context.addApplicationListener(new EmbeddedServerPortFileWriter());
    }

}

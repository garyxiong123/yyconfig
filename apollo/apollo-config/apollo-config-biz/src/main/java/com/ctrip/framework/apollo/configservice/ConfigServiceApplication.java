//package com.ctrip.framework.apollo.configservice;
//
//import com.yofish.apollo.ApolloBizConfig;
//import common.ApolloCommonConfig;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
///**
// * Spring boot application entry point
// *
// * @author Jason Song(song_s@ctrip.com)
// */
//@EnableAutoConfiguration
//@Configuration
//@ComponentScan(basePackageClasses = {ConfigServiceAutoConfiguration.class,ApolloCommonConfig.class, ApolloBizConfig.class, ConfigServiceApplication.class})
//public class ConfigServiceApplication {
//
////    public static void main(String[] args) {
////        ConfigurableApplicationContext context =
////                new SpringApplicationBuilder(ConfigServiceApplication.class).run(args);
////        context.addApplicationListener(new ApplicationPidFileWriter());
//////        context.addApplicationListener(new EmbeddedServerPortFileWriter());
////    }
//
//}

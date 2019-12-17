package controller;

import com.yofish.apollo.JpaApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(excludeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
        LocalAdminServiceApplication.class, JpaApplication.class,
        HttpMessageConverterConfiguration.class})})
@EnableAutoConfiguration
public class AdminServiceTestConfiguration {

}

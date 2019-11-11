package com.yofish.gary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */

@ComponentScan(basePackages = {"com.yofish.gary"})
@EnableJpaAuditing
@Configuration
@SpringBootApplication
public class JpaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(JpaApplication.class, args);
        System.out.println(run);

    }

}

package com.yofish.gary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@SpringBootApplication
public class UmpsShiroApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(UmpsShiroApplication.class, args);
        System.out.println(run);

    }
}


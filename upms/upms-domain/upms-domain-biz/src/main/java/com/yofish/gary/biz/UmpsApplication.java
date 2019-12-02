package com.yofish.gary.biz;

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
public class UmpsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(UmpsApplication.class, args);
        System.out.println(run);

    }
    }


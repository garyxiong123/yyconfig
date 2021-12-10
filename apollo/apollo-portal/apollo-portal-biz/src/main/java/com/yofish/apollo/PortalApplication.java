/*
 *    Copyright 2019-2020 the original author or authors.
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
package com.yofish.apollo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */

@ComponentScan(basePackages = {"com.yofish.gary", "com.yofish.apollo", "com.yofish.apollo.repository"})
@EnableJpaRepositories(basePackages = {"com.yofish.gary", "com.yofish.apollo.repository","com.yofish.apollo.openapi.repository"})
@EnableJpaAuditing
@EntityScan(basePackages = {"com.yofish.apollo.domain","com.yofish.apollo.openapi.entity", "com.yofish.gary"})
@Configuration
@SpringBootApplication
public class PortalApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PortalApplication.class, args);
        System.out.println(run);

    }

}

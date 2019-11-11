package jpa;

import jpa.utils.TestConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@ComponentScan(basePackages = {"jpa"})
@EnableJpaAuditing
@SpringBootApplication
@Configuration
public class JpaApplication {

    //此方法位于一个有@Configuration注解的类中
    @Bean
    public TestConverter testConverter() {
        TestConverter converter = new TestConverter();
        return converter;
    }

    public static void main(String[] args) {
        System.getProperties().setProperty("sprig.application.name", "ss");
        ConfigurableApplicationContext context = SpringApplication.run(JpaApplication.class, args);
        System.out.println("");
    }

}

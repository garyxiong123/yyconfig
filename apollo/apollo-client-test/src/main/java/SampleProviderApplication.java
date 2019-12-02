import apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
//import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by xiongchengwei on 2018/8/20.
 */
@EnableJpaRepositories(basePackages = {"com.yofish.gary", "com.yofish.apollo.repository" })
@EnableJpaAuditing
@EntityScan(basePackages = { "com.yofish.apollo.domain","com.yofish.gary" })
@EnableApolloConfig
@SpringBootApplication
@ComponentScan(value = "com.gary.apollo.test")
public class SampleProviderApplication {


    public static void main(String[] args) throws Exception {
//        System.setProperty("spring.profiles.active", "dev");
//        System.setProperty("env", "DEV");
        System.setProperty("apollo.meta", "http://10.0.33.18:7243");
        ConfigurableApplicationContext run = SpringApplication.run(SampleProviderApplication.class, args);
        System.out.println(run);
    }

}


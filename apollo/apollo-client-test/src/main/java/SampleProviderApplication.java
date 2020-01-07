import apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by xiongchengwei on 2018/8/20.
 */
@EntityScan(basePackages = { "com.yofish.apollo.domain","com.yofish.gary" })
@EnableApolloConfig
@SpringBootApplication
@ComponentScan(value = "com.gary.apollo.test")
public class SampleProviderApplication {


    public static void main(String[] args) throws Exception {
//        System.setProperty("spring.profiles.active", "dev");
//        System.setProperty("env", "DEV");
//        System.setProperty("apollo.meta", "http://10.0.33.18:7243");
//        System.setProperty("apollo.meta", "http://test.lb.gs.youyuwo.com:60008/");
        System.setProperty("apollo.meta", "http://localhost:8080");

        ConfigurableApplicationContext run = SpringApplication.run(SampleProviderApplication.class, args);
        System.out.println(run);
    }

}


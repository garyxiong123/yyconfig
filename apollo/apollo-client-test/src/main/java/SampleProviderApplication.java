import apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
//import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by xiongchengwei on 2018/8/20.
 */

@EnableApolloConfig
@EnableAsync
@EntityScan(basePackages = {"com.youyu.platform.sample.dal.entity"})
@SpringBootApplication
public class SampleProviderApplication {


    public static void main(String[] args) throws Exception {
//        System.setProperty("spring.profiles.active", "dev");
//        System.setProperty("env", "DEV");
//        System.setProperty("apollo.meta", "http://127.0.0.1:8080");
        ConfigurableApplicationContext run = SpringApplication.run(SampleProviderApplication.class, args);
        System.out.println(run);
    }

}


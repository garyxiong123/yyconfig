package biz;

import com.yofish.apollo.ApolloBizConfig;
import common.ApolloCommonConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {ApolloCommonConfig.class, ApolloBizConfig.class})
public class BizTestConfiguration {

}

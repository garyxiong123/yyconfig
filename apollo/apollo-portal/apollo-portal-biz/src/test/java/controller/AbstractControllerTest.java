package controller;

import com.yofish.apollo.JpaApplication;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

//@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AdminServiceTestConfiguration.class, JpaApplication.class})
//@WebIntegrationTest(randomPort = true)
public abstract class AbstractControllerTest {

  @Autowired
  private HttpMessageConverters httpMessageConverters;
  
  RestTemplate restTemplate = new RestTemplate();
//  HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

  @PostConstruct
  private void postConstruct() {
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    restTemplate.setMessageConverters(httpMessageConverters.getConverters());
  }

//  @Value("${local.server.port}")
  int port = 8080;
}

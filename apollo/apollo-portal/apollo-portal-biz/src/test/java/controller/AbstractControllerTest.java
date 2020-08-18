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
package controller;

import com.yofish.apollo.PortalApplication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

//@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {AdminServiceTestConfiguration.class, PortalApplication.class})
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

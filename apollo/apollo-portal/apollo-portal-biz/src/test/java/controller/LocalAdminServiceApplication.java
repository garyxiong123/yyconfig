package controller;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LocalAdminServiceApplication {
  public static void main(String[] args) {
    new SpringApplicationBuilder(LocalAdminServiceApplication.class).run(args);
  }
}

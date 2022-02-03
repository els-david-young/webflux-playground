package com.elsevier.reactivetaskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = "com.elsevier.reactivetaskservice.domain")
@SpringBootApplication
public class ReactiveTaskServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReactiveTaskServiceApplication.class, args);
  }

}

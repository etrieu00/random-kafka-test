package com.etrieu00.requestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication(scanBasePackages = {
  "com.etrieu00.requestservice.*",
  "com.etrieu00.shared.*"
})
public class RequestServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(RequestServiceApplication.class, args);
  }

}

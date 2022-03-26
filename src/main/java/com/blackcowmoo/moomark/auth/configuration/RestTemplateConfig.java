package com.blackcowmoo.moomark.auth.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
  @Bean
  public RestTemplate restTempate() {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }
}

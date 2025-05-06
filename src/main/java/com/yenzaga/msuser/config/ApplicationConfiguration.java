package com.yenzaga.msuser.config;

import com.yenzaga.common.config.AppCoreProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
  @Bean
  @ConfigurationProperties(prefix = "appcore")
  public AppCoreProperties appCoreProperties() {
    return new AppCoreProperties();
  }
}

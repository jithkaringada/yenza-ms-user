package com.yenzaga.msuser.config;

import com.yenzaga.common.config.AppCoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebFluxConfiguration implements WebFluxConfigurer {
  private static final Logger logger = LoggerFactory.getLogger(WebFluxConfiguration.class);

  private final AppCoreProperties appCoreProperties;

  public WebFluxConfiguration(AppCoreProperties appCoreProperties) {
    this.appCoreProperties = appCoreProperties;
  }

  @Bean
  public CorsWebFilter corsWebFilter() {
    UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
    CorsConfiguration configuration = appCoreProperties.getCors();
    if(configuration.getAllowedOrigins() != null && !configuration.getAllowedOrigins().isEmpty()) {
      logger.debug("Registering CORS filter");
      corsConfigurationSource.registerCorsConfiguration("/api/**", configuration);
      corsConfigurationSource.registerCorsConfiguration("/management/**", configuration);
      corsConfigurationSource.registerCorsConfiguration("/v2/api-docs", configuration);
    }
    return new CorsWebFilter(corsConfigurationSource);
  }
}
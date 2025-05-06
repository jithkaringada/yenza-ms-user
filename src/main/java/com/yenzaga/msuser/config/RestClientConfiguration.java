package com.yenzaga.msuser.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClientConfiguration {

  @Autowired
  private SecurityRealmProperties securityRealmProperties;

  @Bean
  @Qualifier("vanillaWebClient")
  public WebClient vanillaWebClient() { return WebClient.create(); }

  @Bean
  public Keycloak keycloakClient() {
    System.out.println("User realm: " + securityRealmProperties.getRealmName());
    return KeycloakBuilder.builder()
        .serverUrl("http://localhost:8080/auth")
        .realm(securityRealmProperties.getRealmName())
        .clientId("yenzagauser_client")
        .clientSecret("ec22ec31-1591-4345-af7e-599f263e6c6d")
        .grantType("client_credentials")
        .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(20).build())
        .build();

  }
}

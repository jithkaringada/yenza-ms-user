package com.yenzaga.msuser.service;

import com.yenzaga.msuser.config.SecurityRealmProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class UserAuthenticationService {
  private final Logger logger = LoggerFactory.getLogger(UserAccountService.class);
  private final WebClient oauth2WebClient;
  private final SecurityRealmProperties securityRealmProperties;

  @Autowired
  public UserAuthenticationService(@Qualifier("oauth2WebClient") WebClient webClient,
                                   SecurityRealmProperties securityRealmProperties) {
    this.oauth2WebClient = webClient;
    this.securityRealmProperties = securityRealmProperties;
  }

  public Mono<String> authenticate(String username, String password) {
    //http://localhost:8080/auth/realms/master/protocol/openid-connect/token
    String templatePath = "/auth/realms/{realm}/protocol/openid-connect/token";
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(securityRealmProperties.getBaseUrl())
        .path(templatePath)
        .build().expand(Collections.singletonMap("realm", securityRealmProperties.getRealmName()));
    String fullauthpath = uriComponents.toUriString();

    logger.info("Get Full user info url: " + fullauthpath);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("username", username);
    body.add("password", password);
    body.add("client_id", "yenzagauser_client");
    body.add("client_secret", "ec22ec31-1591-4345-af7e-599f263e6c6d");
    body.add("grant_type", "password");

    Mono<String> response = oauth2WebClient.post()
        .uri(fullauthpath)
        .header("content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .body(Mono.just(body), MultiValueMap.class)
    .retrieve().bodyToMono(String.class);

    logger.info("Auth response: " + response);
    return response;
  }

  public String refreshAccessToken(String refreshToken) {
    String templatePath = "/auth/realms/{realm}/protocol/openid-connect/token";
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(securityRealmProperties.getBaseUrl())
        .path(templatePath)
        .build().expand(Collections.singletonMap("realm", securityRealmProperties.getRealmName()));
    String fullauthpath = uriComponents.toUriString();

    logger.info("Get Full user info url: " + fullauthpath);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("client_id", "yenzagauser_client");
    body.add("client_secret", "ec22ec31-1591-4345-af7e-599f263e6c6d");
    body.add("grant_type", "refresh_token");
    body.add("refresh_token", refreshToken);

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
    String authresponse = oauth2WebClient
        .post()
        .uri(fullauthpath)
        .body(Mono.just(body), MultiValueMap.class)
        .retrieve()
        .bodyToMono(String.class).block();
    logger.info("Auth response: " + authresponse);
    return authresponse;
  }
}

package com.yenzaga.msuser.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.oauth2.realm", ignoreUnknownFields = false)
public class SecurityRealmProperties {
  private String baseUrl;
  private String realmName;
  private String redirectUrl;

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getRealmName() {
    return realmName;
  }

  public String getRedirectUrl() { return redirectUrl; }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public void setRealmName(String realmName) {
    this.realmName = realmName;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }
}


package com.yenzaga.msuser.domain;

public class Credentials {
  private String username;
  private String password;
  private String client_id;
  private String client_secret;
  private String grant_type;

  public Credentials(String username, String password, String client_id, String client_secret, String grant_type) {
    this.username = username;
    this.password = password;
    this.client_id = client_id;
    this.client_secret = client_secret;
    this.grant_type = grant_type;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getClient_id() {
    return client_id;
  }

  public void setClient_id(String client_id) {
    this.client_id = client_id;
  }

  public String getClient_secret() {
    return client_secret;
  }

  public void setClient_secret(String client_secret) {
    this.client_secret = client_secret;
  }

  public String getGrant_type() {
    return grant_type;
  }

  public void setGrant_type(String grant_type) {
    this.grant_type = grant_type;
  }

  @Override
  public String toString() {
    return "Credentials{" +
        "username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", client_id='" + client_id + '\'' +
        ", client_secret='" + client_secret + '\'' +
        ", grant_type='" + grant_type + '\'' +
        '}';
  }
}

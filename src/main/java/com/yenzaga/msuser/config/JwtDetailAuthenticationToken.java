package com.yenzaga.msuser.config;

import com.yenzaga.common.domain.JwtDetail;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

public class JwtDetailAuthenticationToken extends AbstractAuthenticationToken {
  private JwtDetail jwtDetail;

  public JwtDetailAuthenticationToken(JwtDetail jwtDetail) {
    super(jwtDetail.getRoles().stream().map(r -> new SimpleGrantedAuthority(r)).collect(Collectors.toList()));
    this.jwtDetail = jwtDetail;
    super.setDetails(jwtDetail);
  }

  @Override
  public Object getCredentials() {
    return this.jwtDetail.getAccessToken();
  }

  @Override
  public Object getPrincipal() {
    return this.jwtDetail.getUserId();
  }

  @Override
  public boolean isAuthenticated() {
    return this.jwtDetail.getAuthenticationSucceeded();
  }
}

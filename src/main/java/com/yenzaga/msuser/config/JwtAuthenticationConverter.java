package com.yenzaga.msuser.config;

import com.yenzaga.common.security.AppJwtHelper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public class JwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {
  @Override
  public Mono<AbstractAuthenticationToken> convert(Jwt source) {
    return Mono.just(source).map(r -> new JwtDetailAuthenticationToken(AppJwtHelper.parseJwtString(r.getClaims())));
  }
}

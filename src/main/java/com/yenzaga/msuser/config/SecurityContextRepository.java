package com.yenzaga.msuser.config;

import com.yenzaga.common.domain.JwtDetail;
import com.yenzaga.common.security.AppJwtHelper;
import com.yenzaga.common.security.JwtAuthentication;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityContextRepository.class);

  private static final String TOKEN_PREFIX = "Bearer ";

  @Autowired
  private ReactiveJwtDecoder nimbusReactiveJwtDecoder;

  @Override
  public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
    throw new UnsupportedOperationException("Not supported yet");
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange exchange) {
    ServerHttpRequest request = exchange.getRequest();
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    String authToken = null;
    if(authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
      authToken = authHeader.replace(TOKEN_PREFIX, "");
    } else {
      LOGGER.warn("Couldn't find bearer string, will ignore header.");
    }

    if(authToken != null) {
      Mono<Jwt> monoJwt = nimbusReactiveJwtDecoder.decode(authToken);
      return monoJwt
          .map(j -> {
            JwtDetail jwtDetail = AppJwtHelper.parseJwtString(j.getClaims());
            JwtAuthentication jwtAuthentication = new JwtAuthentication(jwtDetail);
            return new SecurityContextImpl(jwtAuthentication);
          });
    } else {
      return Mono.empty();
    }
  }
}

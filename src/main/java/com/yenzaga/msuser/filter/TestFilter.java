package com.yenzaga.msuser.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class TestFilter implements WebFilter {
  private static final Logger logger = LoggerFactory.getLogger(TestFilter.class);
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    HttpHeaders header = exchange.getRequest().getHeaders();
    return chain.filter(exchange.mutate().build());
  }
}

package com.yenzaga.msuser.config;

import com.yenzaga.common.config.SignatureVerificationProperties;
import com.yenzaga.msuser.filter.TestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class MicroserviceSecurityConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicroserviceSecurityConfiguration.class);
    @Autowired
    @Qualifier("vanillaWebClient")
    private WebClient webClient;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
            .cors().and()
            .formLogin().disable()
            .csrf().disable()
            .httpBasic().disable()
            .headers().frameOptions().disable()
            .and()
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers("/", "/error", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",  "/**/*.html", "/**/*.css", "/**/*.js").permitAll()
            .pathMatchers("/webjars/springfox-swagger-ui/**").permitAll()
            .pathMatchers("/ms/userplatform/api/account/public/**").permitAll()
            .pathMatchers("/ms/userplatform/api/account/secure/**").hasAnyRole("YENZARA", "YENZALO_HOST", "YENZALO_USER")
            .pathMatchers("/ms/userplatform/api/auth/public/**").permitAll()
            .pathMatchers("/ms/userplatform/api/auth/secure/**").hasAnyRole("YENZARA", "YENZALO_HOST", "YENZALO_USER")
            .pathMatchers("/api/user/reset-password/initiate").permitAll()
            .pathMatchers("/api/user/reset-password/complete").permitAll()
            .pathMatchers("/api/user").permitAll()
            .pathMatchers("/api/clearcache").permitAll()
            .pathMatchers("/api/profile-info").permitAll()
            .pathMatchers("/api/management/**").permitAll()
            .pathMatchers("/api/users**").permitAll()
            //.antMatchers("/api/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .pathMatchers("/v2/**").permitAll()
            .pathMatchers("/swagger-resources/**").permitAll()
            .pathMatchers("/swagger-ui.html**").permitAll()
            .and()
            .addFilterBefore(new TestFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .oauth2ResourceServer()
            .jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())
            .and().and().build();
    }


    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }
}

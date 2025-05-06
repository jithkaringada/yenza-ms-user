package com.yenzaga.msuser.util;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.yenzaga.msuser.config.ApplicationConstants.DEFAULT_APP;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                        return springSecurityUser.getUsername();
                    } else if (authentication.getPrincipal() instanceof String) {
                        return (String) authentication.getPrincipal();
                    }
                    return null;
                });
    }

    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> authentication.getAuthorities().stream()
                .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(
                        "ANONYMOUS"))).orElse(false);
    }

    public static boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority)))
                .orElse(false);
    }

    public static String getAppNameFromRequestHeaders(HttpHeaders headers) {
        String yenzaAppToUse = DEFAULT_APP;
        Optional<List<String>> appNameHeader =  headers.entrySet().stream().filter(h -> "yenza-app-name".equals(h.getKey().toLowerCase())).map(e -> e.getValue()).findFirst();
        List<String> yenzaAppInHeaderList = appNameHeader.orElseGet(() -> Arrays.asList(DEFAULT_APP));
        if(yenzaAppInHeaderList.size() > 0) {
            yenzaAppToUse = yenzaAppInHeaderList.get(0);
        }
        return yenzaAppToUse;
    }
}

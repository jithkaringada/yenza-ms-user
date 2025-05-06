package com.yenzaga.msuser.controller;

import com.yenzaga.msuser.domain.UserCredential;
import com.yenzaga.msuser.service.UserAuthenticationService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/ms/userplatform/api/auth")
public class UserAuthenticationController {
  private final UserAuthenticationService userAuthenticationService;

  @Autowired
  public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
    this.userAuthenticationService = userAuthenticationService;
  }

  @RequestMapping(value = "/public/authenticate", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
  @Timed
  @CrossOrigin
  public Mono<ResponseEntity<String>> authenticate(@ModelAttribute UserCredential auth) {
    return userAuthenticationService.authenticate(auth.getUsername(), auth.getPassword())
        .map(r -> ResponseEntity.ok(r));

  }

  @RequestMapping(value = "/public/refresh-access-token", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE } )
  @Timed
  @CrossOrigin
  public ResponseEntity<String> refreshAccessToken(@RequestParam Map<String, String> refreshTokenParams) {
    String authResponse = userAuthenticationService.refreshAccessToken(refreshTokenParams.get("refresh_token"));
    return ResponseEntity.ok(authResponse);
  }
}

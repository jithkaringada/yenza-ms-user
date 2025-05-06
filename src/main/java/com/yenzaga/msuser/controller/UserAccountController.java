package com.yenzaga.msuser.controller;
;
import com.yenzaga.common.domain.EmailStreamMessage;
import com.yenzaga.common.domain.RestResponse;
import com.yenzaga.common.utils.HeaderUtil;
import com.yenzaga.msuser.domain.User;
import com.yenzaga.msuser.service.EmailSenderService;
import com.yenzaga.msuser.service.UserAccountService;
import com.yenzaga.msuser.util.SecurityUtil;
import com.yenzaga.msuser.vm.ManagedUserVM;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/ms/userplatform/api/account")
public class UserAccountController {
  private final Logger logger = LoggerFactory.getLogger(UserAccountController.class);

  private final UserAccountService userAccountService;
  private final EmailSenderService emailSenderService;

  @Autowired
  public UserAccountController(UserAccountService userAccountService, EmailSenderService emailSenderService) {
    this.userAccountService = userAccountService;
    this.emailSenderService = emailSenderService;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/public/register")
  @Timed
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RestResponse> registerUserAccount(@Valid @RequestBody ManagedUserVM managedUserVM,
                                                          @RequestHeader HttpHeaders headers) {
    String yenzaAppToUse = SecurityUtil.getAppNameFromRequestHeaders(headers);
    RestResponse restResponse = new RestResponse();
    try {
      User user = userAccountService.registerUser(managedUserVM, managedUserVM.getPassword(), yenzaAppToUse);
      EmailStreamMessage emailStreamMessage = new EmailStreamMessage();
      emailStreamMessage.setEmailType(EmailStreamMessage.EmailContentType.ACCOUNT_ACTIVATION_EMAIL);
      emailStreamMessage.setUser(user.toUserEmailTransformObject(yenzaAppToUse));
      emailSenderService.sendEmailEvent(emailStreamMessage);
      restResponse.setCode(HttpStatus.CREATED.value());
      restResponse.setMessage(user.getFirstName() + " " + user.getLastName() + " account created.");
      restResponse.setHasError(false);
      return ResponseEntity.created(new URI("/api/account/secure/user-data?email=" + managedUserVM.getEmail()))
          .headers(HeaderUtil.createAlert("userManagement.created", user.getLogin()))
          .body(restResponse);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      restResponse.setCode(HttpStatus.BAD_REQUEST.value());
      restResponse.setMessage(managedUserVM.getFirstName() + " " + managedUserVM.getLastName() + " account not created. " + e.getMessage());
      restResponse.setHasError(true);
      return ResponseEntity.ok()
          .headers(HeaderUtil.createFailureAlert(managedUserVM.getLogin(), "userManagement.created", "failure creating user"))
          .body(restResponse);
    }
  }

  @RequestMapping(value = "/public/reset-password", method = RequestMethod.PUT)
  @Timed
  @CrossOrigin
  public void resetPassword(@RequestBody Map<String, String> resetparams) {
    List<User> usersByEmail = userAccountService.getUserInfoByEmail(resetparams.get("email"));
    Optional<User> firstUser = Optional.ofNullable(usersByEmail.size() > 0 ? usersByEmail.get(0) : null);
    if(firstUser.isPresent()) {
      userAccountService.resetPassword(firstUser.get().getId());
    } else {
      throw new UsernameNotFoundException("Email does not exist!");
    }
  }

  @RequestMapping(value = "/secure/user-data", method = RequestMethod.GET)
  @Timed
  @CrossOrigin
  public ResponseEntity<User> getUserInfo(@RequestParam("email") String email) {
    logger.info("Email passed in: " + email);
    List<User> usersByEmail = userAccountService.getUserInfoByEmail(email);
    Optional<User> firstUser = Optional.ofNullable(usersByEmail.size() > 0 ? usersByEmail.get(0) : null);
    if(firstUser.isPresent()) {
      return ResponseEntity.ok(firstUser.get());
    } else {
      throw new UsernameNotFoundException("Email does not exist!");
    }
  }

  @RequestMapping(value = "/secure/user-data-by-id/{id}", method = RequestMethod.GET)
  @Timed
  @CrossOrigin
  public Mono<ResponseEntity<User>> getUserInfoById(@PathVariable("id") String id) {
    logger.info("Email passed in: " + id);
    return userAccountService.getUserInfoByUserId(id).map(r -> ResponseEntity.ok(r))
        .defaultIfEmpty(ResponseEntity.notFound().<User>build());
  }
}

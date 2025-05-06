package com.yenzaga.msuser.service;

import com.yenzaga.common.utils.RandomUtil;
import com.yenzaga.msuser.config.ApplicationConstants;
import com.yenzaga.msuser.config.SecurityRealmProperties;
import com.yenzaga.msuser.domain.AppRoleMappingDetail;
import com.yenzaga.msuser.domain.User;
import com.yenzaga.msuser.exception.*;
import com.yenzaga.msuser.repository.AppRoleRepository;
import com.yenzaga.msuser.vm.UserDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAccountService extends BaseService {
  private final Logger logger = LoggerFactory.getLogger(UserAccountService.class);

  private final Keycloak keycloak;
  private final WebClient webClient;
  private final SecurityRealmProperties securityRealmProperties;
  private final AppRoleRepository appRoleRepository;

  String USERS_BY_LOGIN_CACHE = "usersByLogin";
  String USERS_BY_EMAIL_CACHE = "usersByEmail";

  @Autowired
  public UserAccountService(Keycloak keycloak, WebClient webClient,
                            SecurityRealmProperties securityRealmProperties,
                            AppRoleRepository appRoleRepository) {
    super();
    this.webClient = webClient;
    this.keycloak = keycloak;
    this.securityRealmProperties = securityRealmProperties;
    this.appRoleRepository = appRoleRepository;
  }

  public User registerUser(UserDTO userDTO, String password, String appName) {
    if(!allowPasswordLength(password)) {
      throw new InvalidPasswordException();
    }
    //find user with user email from keycloak rest call
    List<UserRepresentation> userRepresentationsByLogin = keycloak.realm(securityRealmProperties.getRealmName())
        .users().search(userDTO.getLogin());
    if(userRepresentationsByLogin.size() > 0) {
      throw new LoginAlreadyUsedException(userDTO.getLogin());
    }

    List<UserRepresentation> userRepresentationsByEmail = keycloak.realm(securityRealmProperties.getRealmName())
        .users().search(null, null, null, userDTO.getEmail(), 0, 1);
    if(userRepresentationsByEmail.size() > 0) {
      throw new EmailAlreadyUsedException(userDTO.getEmail());
    }

    Response response = keycloak.realm(securityRealmProperties.getRealmName()).users().create(userDTO.toKeycloakUserRepresentation(password));
    if(response.getStatus() != 201) {
      logger.error("Could not create the user: " + userDTO.getLogin());
      throw new BadRequestAlertException("Failed to create user due to " + response.getStatus(), userDTO.getLogin(), "userCreateFailure");
    }
    //Create new user from front end
    User newUser = convertUserDTOtoUser(userDTO, password);

    //create user using keycloak API and keep it disabled until user activates
    //store application registered with in the user attributes field

    logger.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public List<User> getUserInfoByEmail(String email) {
    //https://yenzaga.ngrok.io/auth/admin/realms/yenzaga/users
    String templatePath = "/auth/admin/realms/{realm}/users";
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(securityRealmProperties.getBaseUrl())
        .path(templatePath)
        .queryParam("email", email)
        .build().expand(Collections.singletonMap("realm", securityRealmProperties.getRealmName()));
    String fullGetUserInfoPath = uriComponents.toUriString();
    System.out.println("Get Full user info url: " + fullGetUserInfoPath);
    User[] usersResponse = webClient.get()
        .uri(fullGetUserInfoPath)
        .retrieve()
        .bodyToMono(User[].class)
        .block();

    List<User> usersFoundList = usersResponse != null ? Arrays.asList(usersResponse) : Collections.emptyList();
    if(usersFoundList.isEmpty()) {
      logger.warn("User with email " + email + " is not found");
    } else {
      logger.info("User found with this info: " + usersFoundList.get(0));
    }
    return usersFoundList;
  }

  public Mono<User> getUserInfoByUserId(String id) {
    String templateGetUserInfoPath = "/auth/admin/realms/{realm}/users/{id}";
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(securityRealmProperties.getBaseUrl())
        .path(templateGetUserInfoPath)
        .build().expand(Map.of("realm", securityRealmProperties.getRealmName(),
            "id", id));
    String fullGetUserInfoPath = uriComponents.toUriString();
    System.out.println("Get Full user info url: " + fullGetUserInfoPath);
    Mono<UserRepresentation> userRepresentationMono = webClient.get()
        .uri(fullGetUserInfoPath)
        .retrieve()
        .bodyToMono(UserRepresentation.class);

    String templateRoleMappingsPath = "/auth/admin/realms/{realm}/users/{id}/role-mappings";
    UriComponents uriComponentsRoleMappings = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(securityRealmProperties.getBaseUrl())
        .path(templateRoleMappingsPath)
        .build().expand(Map.of("realm", securityRealmProperties.getRealmName(), "id", id));
    String fullRoleMappingsPath = uriComponentsRoleMappings.toUriString();
    System.out.println("Get Full role mappings url: " + fullRoleMappingsPath);
    Mono<MappingsRepresentation> mappingsRepresentationMono = webClient.get()
        .uri(fullRoleMappingsPath)
        .retrieve()
        .bodyToMono(MappingsRepresentation.class);
        //.map(r -> User.of(r));

    return userRepresentationMono.zipWith(mappingsRepresentationMono)
        .map(tuple2 -> {
          User user = User.of(tuple2.getT1());
          if(tuple2.getT2().getRealmMappings() != null && tuple2.getT2().getRealmMappings().size() > 0) {
            user.setRoles(tuple2.getT2().getRealmMappings().stream().map(r -> r.getName()).collect(Collectors.toList()));
            user.setAppRoleMappingDetailList(tuple2
                .getT2()
                .getRealmMappings()
                .stream()
                .map(r -> AppRoleMappingDetail.of(r.getName())).collect(Collectors.toList()));
          }
          return user;
        });
  }

  public void resetPassword(String userId) {
    String templatePath = "/auth/admin/realms/{realm}/users/{userId}/execute-actions-email";
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(securityRealmProperties.getBaseUrl())
        .path(templatePath)
        .build().expand(Map.of("realm", securityRealmProperties.getRealmName(), "userId", userId));
    String fullResetPasswordPath = uriComponents.toUriString();
    System.out.println("Full reset password path: " + fullResetPasswordPath);
    webClient.put()
        .uri(fullResetPasswordPath)
        .body(Mono.just(new String[]{ "UPDATE_PASSWORD" }), String[].class);
    //oAuth2RestTemplate.put(fullResetPasswordPath, );
  }

  public Flux<AppRoleMappingDetail> loadKeycloakRoleDetailsToUserPlatform() {
    String templatePath = "/auth/realms/{realm}/roles";
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(securityRealmProperties.getBaseUrl())
        .path(templatePath)
        .build().expand(Map.of("realm", securityRealmProperties.getRealmName()));
    String fullGetRolesUrl = uriComponents.toUriString();
    System.out.println("Full get roles path: " + fullGetRolesUrl);
    return webClient.get()
        .uri(fullGetRolesUrl)
        .retrieve()
        .bodyToFlux(RoleRepresentation.class)
        .map(r -> {
          AppRoleMappingDetail appRoleMappingDetail = new AppRoleMappingDetail();
          String[] rolenameParts = r.getName().split("_");
          if (rolenameParts.length >= 2) {
            appRoleMappingDetail.setId(r.getName());
            appRoleMappingDetail.setAppName(rolenameParts[1]);
            appRoleMappingDetail.setRoleId(r.getName());
            if (rolenameParts.length == 3) {
              appRoleMappingDetail.setRoleName(rolenameParts[1] + " " + rolenameParts[2]);
            } else {
              appRoleMappingDetail.setRoleName(rolenameParts[1] + " USER");
            }
          }
          return appRoleRepository.findById(appRoleMappingDetail.getId())
              .switchIfEmpty(appRoleRepository.insert(appRoleMappingDetail));
        }).flatMap(r -> r);
  }

  public Flux<AppRoleMappingDetail> getRoleDetails() {
    return appRoleRepository.findAll();
  }

  public User registerUserWithAnotherYenzaApp(String login, String email, String appName)
      throws AccountNotFoundException {
    //find email in the realm
    return new User();
  }

  private User updateActivationInformationForUser(String appName, User user) {
    if (ApplicationConstants.ApplicationNames.YENZALO.toString().equalsIgnoreCase(appName)) {
      user.setYenzacayActivated(false);
      user.setYenzacayActivationKey(RandomUtil.generateActivationKey());
      //update user attributes in keycloak
    } else if (ApplicationConstants.ApplicationNames.YENZACART.toString()
        .equalsIgnoreCase(appName)) {
      user.setYenzacayActivated(false);
      user.setYenzacartActivationKey(RandomUtil.generateActivationKey());
    }
    return user;
  }

  public Optional<User> activateRegistration(String appName, String key) {
    logger.debug("Activating user on app {} with activation key {}", appName, key);
    if(ApplicationConstants.ApplicationNames.YENZALO.toString().equals(appName.toUpperCase())) {
      //activate yenzalo and NULL yenzalo key
    } else if(ApplicationConstants.ApplicationNames.YENZACART.toString().equals(appName.toUpperCase())) {
      //activate yenzacart and NULL yenzacart activation key in attributes
    } else {
      return Optional.empty();
    }
    return Optional.of(new User());
  }

  public void changePassword(String password) {
   //update password in keycloak
  }
}

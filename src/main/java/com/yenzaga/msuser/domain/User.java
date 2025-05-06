package com.yenzaga.msuser.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yenzaga.common.domain.UserETO;
import com.yenzaga.msuser.config.ApplicationConstants;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Pattern(regexp = ApplicationConstants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @JsonIgnore
    @NotNull
    @Size(max = 100)
    private String password;

    @Size(max = 200)
    private String firstName;

    @Size(max = 200)
    private String lastName;

    @Email
    @Size(max = 400)
    private String email;

    private boolean yenzacayActivated = false;

    private boolean yenzacartActivated = false;

    @Size(min = 2, max = 6)
    private String langCode;

    @Size(max = 400)
    private String imageUrl;

    @Size(max = 40)
    @JsonIgnore
    private String yenzacayActivationKey;

    @Size(max = 40)
    @JsonIgnore
    private String yenzacartActivationKey;

    @Size(max = 40)
    @JsonIgnore
    private String resetKey;

    private Instant resetDate = null;

    private List<String> roles = new ArrayList<>();

    @JsonIgnore
    private Set<Authority> authorities = new HashSet<>();

    private List<AppRoleMappingDetail> appRoleMappingDetailList = new ArrayList<>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isYenzacayActivated() {
        return yenzacayActivated;
    }

    public void setYenzacayActivated(boolean yenzacayActivated) { this.yenzacayActivated = yenzacayActivated; }

    public boolean isYenzacartActivated() { return yenzacartActivated; }

    public void setYenzacartActivated(boolean yenzacartActivated) { this.yenzacartActivated = yenzacartActivated; }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getYenzacayActivationKey() {
        return yenzacayActivationKey;
    }

    public void setYenzacayActivationKey(String yenzacayActivationKey) { this.yenzacayActivationKey = yenzacayActivationKey; }

    public String getYenzacartActivationKey() { return yenzacartActivationKey; }

    public void setYenzacartActivationKey(String yenzacartActivationKey) { this.yenzacartActivationKey = yenzacartActivationKey; }

    public String getResetKey() { return resetKey; }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Instant getResetDate() {
        return resetDate;
    }

    public void setResetDate(Instant resetDate) {
        this.resetDate = resetDate;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public List<AppRoleMappingDetail> getAppRoleMappingDetailList() {
        return appRoleMappingDetailList;
    }

    public void setAppRoleMappingDetailList(List<AppRoleMappingDetail> appRoleMappingDetailList) {
        this.appRoleMappingDetailList = appRoleMappingDetailList;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public static User of(UserRepresentation ur) {
        User user = new User();
        user.setId(ur.getId());
        user.setEmail(ur.getEmail());
        user.setFirstName(ur.getFirstName());
        user.setLastName(ur.getLastName());
        if(ur.getRealmRoles() != null && ur.getRealmRoles().size() > 0) {
            user.setRoles(ur.getRealmRoles());
            user.setAppRoleMappingDetailList(ur.getRealmRoles()
                .stream()
                .map(r -> AppRoleMappingDetail.of(r))
                .collect(Collectors.toList()));
        }
        return user;
    }

    public UserETO toUserEmailTransformObject(String appName) {
        UserETO userETO = new UserETO();
        userETO.setActivationKey(this.getYenzacayActivationKey());
        userETO.setEmailAddress(this.getEmail());
        userETO.setFirstName(this.getFirstName());
        userETO.setLastName(this.getLastName());
        userETO.setLangKey(this.getLangCode());
        userETO.setLogin(this.getLogin());
        userETO.setResetKey(this.getResetKey());
        userETO.setAppName(appName);
        return userETO;
    }

    public List<String> getRegisteredApps() {
        List<String> appnames = new ArrayList<>();
        if(this.isYenzacayActivated()) {
            appnames.add(ApplicationConstants.ApplicationNames.YENZALO.toString());
        }
        if(this.isYenzacartActivated()) {
            appnames.add(ApplicationConstants.ApplicationNames.YENZACART.toString());
        }
        return appnames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(login, user.login) && Objects.equals(firstName, user.firstName)
                && Objects.equals(lastName, user.lastName) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return "User{" + "login='" + login + '\'' + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\'' + ", email='" + email + '\''
                + ", yenzacayActivated=" + yenzacayActivated + ", yenzacartActivated="
                + yenzacartActivated + ", imageUrl='" + imageUrl + '\'' + ", authorities="
                + authorities
                + '\'' + '}' + ", registeredApps='" + getRegisteredApps()
                + '\'' + '}';
    }
}

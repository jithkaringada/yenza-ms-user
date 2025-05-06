package com.yenzaga.msuser.service;

import com.yenzaga.msuser.config.ApplicationConstants;
import com.yenzaga.msuser.domain.Authority;
import com.yenzaga.msuser.domain.User;
import com.yenzaga.msuser.vm.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BaseService {

    private final Logger logger = LoggerFactory.getLogger(BaseService.class);

    protected boolean allowPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
                password.length() >= ApplicationConstants.PASSWORD_MIN_LENGTH &&
                password.length() <= ApplicationConstants.PASSWORD_MAX_LENGTH;
    }

    protected User convertUserDTOtoUser(UserDTO userDTO, String password) {
        User newUser = new User();
        Set<Authority> authorities = new HashSet<>();
        if(userDTO.getAuthorities() == null) {
            Authority userAuthority = new Authority();
            userAuthority.setName("YENZARA");
            authorities.add(userAuthority);
        } else {
            for (String authname : userDTO.getAuthorities()) {
                Authority exampleAuthority = new Authority();
                exampleAuthority.setName(authname);
                authorities.add(exampleAuthority);
            }
        }
        newUser.setAuthorities(authorities);
        newUser.setId(UUID.randomUUID().toString());
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangCode(userDTO.getLangKey());
        newUser.setLogin(userDTO.getLogin());

        return newUser;
    }

}

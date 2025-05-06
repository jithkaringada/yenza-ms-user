package com.yenzaga.msuser.exception;

import org.apache.commons.lang3.StringUtils;

public class LoginAlreadyUsedException extends BadRequestAlertException {
    public LoginAlreadyUsedException(String login) {
        super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, StringUtils.isEmpty(login) ? "Login already in use. " : "Login " + login + " is already in use", "userManagement", "userexists");
    }

    public LoginAlreadyUsedException() {
        this(null);
    }
}

package com.yenzaga.msuser.exception;

import org.apache.commons.lang.StringUtils;

public class EmailAlreadyUsedException extends BadRequestAlertException {
    public EmailAlreadyUsedException(String emailAddress) {
        super(ErrorConstants.EMAIL_ALREADY_USED_TYPE, StringUtils.isEmpty(emailAddress) ? "Email address already in use. " : "Email address " + emailAddress + " is already in use.", "userManagement", "emailexists");
    }

    public EmailAlreadyUsedException() {
        this(null);
    }
}

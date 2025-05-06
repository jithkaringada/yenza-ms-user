package com.yenzaga.msuser.exception;

import org.apache.commons.lang.StringUtils;

public class AccountNotFoundException extends BadRequestAlertException {
    public AccountNotFoundException(String additionalMessage) {
        super(ErrorConstants.EMAIL_ALREADY_USED_TYPE, StringUtils.isEmpty(additionalMessage) ? "Account is not found. Please register new account. " : additionalMessage, "userManagement", "emailexists");
    }

    public AccountNotFoundException() {
        this(null);
    }
}

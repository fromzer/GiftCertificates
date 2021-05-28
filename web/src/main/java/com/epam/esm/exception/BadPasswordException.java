package com.epam.esm.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class BadPasswordException extends BadCredentialsException {

    public BadPasswordException(String msg) {
        super(msg);
    }
}
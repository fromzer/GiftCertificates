package com.epam.esm.exception;

import org.springframework.security.authentication.LockedException;

public class UserBlockedException extends LockedException {

    public UserBlockedException(String msg) {
        super(msg);
    }
}

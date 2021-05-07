package com.epam.esm.exception;

public class DeleteResourceException extends RuntimeException {
    public DeleteResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

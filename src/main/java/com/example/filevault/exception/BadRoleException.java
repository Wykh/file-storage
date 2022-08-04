package com.example.filevault.exception;

public class BadRoleException extends RuntimeException {
    public BadRoleException(String message) {
        super(message);
    }

    public BadRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRoleException(Throwable cause) {
        super(cause);
    }
}

package com.example.file.vault.exception;

public class EmptyGetParamException extends RuntimeException {
    public EmptyGetParamException(String message) {
        super(message);
    }

    public EmptyGetParamException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.example.fileVault.exception;

public class IncorrectDateTypeFilterParamException extends RuntimeException {
    public IncorrectDateTypeFilterParamException(String message) {
        super(message);
    }

    public IncorrectDateTypeFilterParamException(String message, Throwable cause) {
        super(message, cause);
    }
}

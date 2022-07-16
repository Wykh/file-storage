package com.example.file.vault.exception;

public class IncorrectFilterParamException extends RuntimeException {
    public IncorrectFilterParamException(String message) {
        super(message);
    }

    public IncorrectFilterParamException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.example.filevault.exception;

public class IncorrectFilterParamException extends RuntimeException {
    public IncorrectFilterParamException(String message) {
        super(message);
    }

    public IncorrectFilterParamException(String message, Throwable cause) {
        super(message, cause);
    }
}

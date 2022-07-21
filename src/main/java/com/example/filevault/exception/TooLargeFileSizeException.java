package com.example.filevault.exception;

public class TooLargeFileSizeException extends RuntimeException {

    public TooLargeFileSizeException(String message) {
        super(message);
    }

    public TooLargeFileSizeException(String message, Throwable cause) {
        super(message, cause);
    }
}

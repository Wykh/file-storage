package com.example.file.vault.exception;

public class CloseStreamException extends RuntimeException{
    public CloseStreamException(String message) {
        super(message);
    }

    public CloseStreamException(String message, Throwable cause) {
        super(message, cause);
    }
}

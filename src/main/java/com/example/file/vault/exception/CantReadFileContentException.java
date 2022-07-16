package com.example.file.vault.exception;

public class CantReadFileContentException extends RuntimeException {
    public CantReadFileContentException(String message) {
        super(message);
    }

    public CantReadFileContentException(String message, Throwable cause) {
        super(message, cause);
    }
}

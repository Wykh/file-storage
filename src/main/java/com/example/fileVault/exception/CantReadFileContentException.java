package com.example.fileVault.exception;

public class CantReadFileContentException extends RuntimeException {
    public CantReadFileContentException(String message) {
        super(message);
    }
}

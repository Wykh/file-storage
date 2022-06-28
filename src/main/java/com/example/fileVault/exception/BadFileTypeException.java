package com.example.fileVault.exception;

public class BadFileTypeException extends RuntimeException{
    public BadFileTypeException(String message) {
        super(message);
    }
}

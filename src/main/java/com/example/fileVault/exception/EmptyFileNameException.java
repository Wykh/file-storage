package com.example.fileVault.exception;

public class EmptyFileNameException extends RuntimeException{
    public EmptyFileNameException(String message) {
        super(message);
    }
}

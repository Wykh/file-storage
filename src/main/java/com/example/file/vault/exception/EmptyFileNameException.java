package com.example.file.vault.exception;

public class EmptyFileNameException extends RuntimeException{
    public EmptyFileNameException(String message) {
        super(message);
    }
}

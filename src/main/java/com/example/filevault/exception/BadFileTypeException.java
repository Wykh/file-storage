package com.example.filevault.exception;

public class BadFileTypeException extends RuntimeException{
    public BadFileTypeException(String message) {
        super(message);
    }
}

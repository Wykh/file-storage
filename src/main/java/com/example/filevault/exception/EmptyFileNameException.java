package com.example.filevault.exception;

public class EmptyFileNameException extends RuntimeException{
    public EmptyFileNameException(String message) {
        super(message);
    }
}

package com.example.fileVault.exception;

public class EmptyFileListException extends RuntimeException{
    public EmptyFileListException(String message) {
        super(message);
    }
}

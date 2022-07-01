package com.example.fileVault.exception;

public class ReadWriteStreamException extends RuntimeException{
    public ReadWriteStreamException(String message) {
        super(message);
    }

    public ReadWriteStreamException(String message, Throwable cause) {
        super(message, cause);
    }
}

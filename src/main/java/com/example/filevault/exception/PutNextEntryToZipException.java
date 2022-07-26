package com.example.filevault.exception;

public class PutNextEntryToZipException extends RuntimeException{

    public PutNextEntryToZipException(String message) {
        super(message);
    }

    public PutNextEntryToZipException(String message, Throwable cause) {
        super(message, cause);
    }
}

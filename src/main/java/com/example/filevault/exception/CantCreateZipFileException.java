package com.example.filevault.exception;

import java.io.IOException;

public class CantCreateZipFileException extends RuntimeException {
    public CantCreateZipFileException(String message) {
        super(message);
    }

    public CantCreateZipFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CantCreateZipFileException(Throwable cause) {
        super(cause);
    }
}

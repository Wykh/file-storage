package com.example.filevault.exception;

public class DeletingFileNotExistsInStorageException extends RuntimeException {
    public DeletingFileNotExistsInStorageException(String message) {
        super(message);
    }

    public DeletingFileNotExistsInStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeletingFileNotExistsInStorageException(Throwable cause) {
        super(cause);
    }
}

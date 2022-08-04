package com.example.filevault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebRestControllerAdvice {
    @ExceptionHandler({
            CloseStreamException.class, PutNextEntryToZipException.class,
            ReadWriteStreamException.class,
            IncorrectFilterParamException.class,
            EmptyGetParamException.class,
            IncorrectDateTypeFilterParamException.class})
    public ResponseEntity<String> handleWtfException(Exception e) {
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<String> handleForbiddenException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler({FileNotFoundException.class,
            DeletingFileNotExistsInStorageException.class})
    public ResponseEntity<String> handleFileNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
    }

    @ExceptionHandler({BadFileTypeException.class, EmptyFileNameException.class, CantReadFileContentException.class})
    public ResponseEntity<String> handleFileNotSaved(Exception e) {
        return ResponseEntity.badRequest().body("File is not saved! Error: " + e.getMessage());
    }

    @ExceptionHandler(EmptyFileListException.class)
    public ResponseEntity<String> handleEmptyFileListException(Exception e) {
        return ResponseEntity.badRequest().body("Error reading all files: " + e.getMessage());
    }

    @ExceptionHandler(TooLargeFileSizeException.class)
    public ResponseEntity<String> handleTooLargeFileSizeException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

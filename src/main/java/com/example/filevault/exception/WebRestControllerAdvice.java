package com.example.filevault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WebRestControllerAdvice {
    @ExceptionHandler({IncorrectFilterParamException.class})
    public ResponseEntity<String> handleWtfException(Exception e) {
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(e.getMessage());
    }

    @ExceptionHandler({ForbiddenException.class,
            BadRoleException.class})
    public ResponseEntity<String> handleForbiddenException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<String> handleUnauthorizedException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler({FileNotFoundException.class,
            DeletingFileNotExistsInStorageException.class})
    public ResponseEntity<String> handleFileNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
    }

    @ExceptionHandler({TooLargeFileSizeException.class,
            BadFileTypeException.class, EmptyFileNameException.class})
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({CantReadFileContentException.class,
            CantCreateZipFileException.class})
    public ResponseEntity<String> handleInternalServerExceptions(Exception e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

}

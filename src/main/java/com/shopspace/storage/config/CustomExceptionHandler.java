package com.shopspace.storage.config;

import com.shopspace.storage.exception.FileExtensionException;
import com.shopspace.storage.exception.FileSizeException;
import com.shopspace.storage.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FileExtensionException.class)
    public ResponseEntity<String> conflict(FileExtensionException ex) {
        return new ResponseEntity<>("FileExtensionException", HttpStatus.OK);
    }

    @ExceptionHandler(FileSizeException.class)
    public ResponseEntity<String> conflict(FileSizeException ex) {
        return new ResponseEntity<>("FileSizeException", HttpStatus.OK);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> conflict(ResourceNotFoundException ex) {
        return new ResponseEntity<>("ResourceNotFoundException", HttpStatus.OK);
    }
}

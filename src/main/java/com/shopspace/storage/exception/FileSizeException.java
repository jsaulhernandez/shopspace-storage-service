package com.shopspace.storage.exception;

public class FileSizeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FileSizeException(String message) {
        super(message);
    }
}

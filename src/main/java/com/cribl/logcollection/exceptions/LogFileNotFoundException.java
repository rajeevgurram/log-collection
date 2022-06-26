package com.cribl.logcollection.exceptions;

public class LogFileNotFoundException extends RuntimeException {
    private final String fileName;

    public LogFileNotFoundException(final String fileName) {
        super();
        this.fileName = fileName;
    }

    @Override
    public String getMessage() {
        return "file: " + fileName + " not found";
    }
}

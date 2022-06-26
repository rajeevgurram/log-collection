package com.cribl.logcollection.handlers;

import com.cribl.logcollection.exceptions.LogFileNotFoundException;
import com.cribl.logcollection.pojo.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {LogFileNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse logFileNotFoundHandler(LogFileNotFoundException exception) {
        return ErrorResponse.builder()
                .error(true)
                .errorMessage(exception.getMessage())
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .build();
    }
}

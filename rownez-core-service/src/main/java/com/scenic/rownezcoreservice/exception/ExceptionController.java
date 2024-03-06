package com.scenic.rownezcoreservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> apiExceptionHandler(ApiException apiException) {
        return new ResponseEntity<>(apiException.getMessage(), apiException.getStatus());
    }
}

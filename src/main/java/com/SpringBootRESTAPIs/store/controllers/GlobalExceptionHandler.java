package com.SpringBootRESTAPIs.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // this annotation is used to handle exceptions globally in the application, it will catch any exception thrown by any controller and handle it in this class
public class GlobalExceptionHandler {
    // handling MethodArgumentNotValidException of the validation errors of the request body(see post method), we return appropriate validation errors in the body.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentValidationErrors(
            MethodArgumentNotValidException exception
    ){
        Map<String, String> errors = new HashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}

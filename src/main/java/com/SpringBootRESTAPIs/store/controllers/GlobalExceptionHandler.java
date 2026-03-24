package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.ErrorDto;
import com.SpringBootRESTAPIs.store.exceptions.CartNotFoundException;
import com.SpringBootRESTAPIs.store.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    // handling error for when the cartId for checkout is not a UUID.
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorDto> handleUnreadableMessage(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto("Invalid request body!"));
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCartNotFoundException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto("Cart not found!"));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorDto> handleProductNotFoundException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto("Product not found in the cart!"));
    }
}

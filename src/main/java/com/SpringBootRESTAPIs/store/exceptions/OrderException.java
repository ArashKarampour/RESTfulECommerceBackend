package com.SpringBootRESTAPIs.store.exceptions;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}

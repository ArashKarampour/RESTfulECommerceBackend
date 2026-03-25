package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.ErrorDto;
import com.SpringBootRESTAPIs.store.dtos.OrderDto;
import com.SpringBootRESTAPIs.store.exceptions.OrderException;
import com.SpringBootRESTAPIs.store.mappers.OrderMapper;
import com.SpringBootRESTAPIs.store.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public List<OrderDto> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable(name = "id") Long id){
        var order = orderService.getOrderById(id);
        return orderMapper.toDto(order);

    }

    @ExceptionHandler(OrderException.class)
    private ResponseEntity<ErrorDto> handleOrderException(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    private ResponseEntity<ErrorDto> handleAccessDeniedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto(ex.getMessage()));
    }

}

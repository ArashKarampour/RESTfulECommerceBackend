package com.SpringBootRESTAPIs.store.services;

import com.SpringBootRESTAPIs.store.dtos.ErrorDto;
import com.SpringBootRESTAPIs.store.dtos.OrderDto;
import com.SpringBootRESTAPIs.store.entities.Order;
import com.SpringBootRESTAPIs.store.exceptions.OrderException;
import com.SpringBootRESTAPIs.store.mappers.OrderMapper;
import com.SpringBootRESTAPIs.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class OrderService {

    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    public List<OrderDto> getOrders() {
        var user = authService.getCurrentUser();
        var orders = orderRepository.findAllByCustomer(user);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    public Order getOrderById(Long id) {
        var order = orderRepository.getOrderWithItems(id).orElse(null);
        if(order == null)
            throw new OrderException("Order Not Found!");

        var user = authService.getCurrentUser();
        if(!order.isForCustomer(user))
            throw new AccessDeniedException("You don't have access to this order!");

        return order;
    }

}
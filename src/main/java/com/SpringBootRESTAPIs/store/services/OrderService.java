package com.SpringBootRESTAPIs.store.services;

import com.SpringBootRESTAPIs.store.dtos.OrderDto;
import com.SpringBootRESTAPIs.store.mappers.OrderMapper;
import com.SpringBootRESTAPIs.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
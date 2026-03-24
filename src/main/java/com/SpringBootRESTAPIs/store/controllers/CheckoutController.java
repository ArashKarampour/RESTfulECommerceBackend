package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.CheckoutRequest;
import com.SpringBootRESTAPIs.store.dtos.CheckoutResponse;
import com.SpringBootRESTAPIs.store.entities.Order;
import com.SpringBootRESTAPIs.store.entities.OrderItem;
import com.SpringBootRESTAPIs.store.entities.OrderStatus;
import com.SpringBootRESTAPIs.store.repositories.CartRepository;
import com.SpringBootRESTAPIs.store.repositories.OrderRepository;
import com.SpringBootRESTAPIs.store.services.AuthService;
import com.SpringBootRESTAPIs.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;


    @PostMapping
    public ResponseEntity<?> checkout(
            @Valid @RequestBody CheckoutRequest request
    ){
        var cart =  cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if(cart == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "Cart doesn't exist!")
            );
        }

        if(cart.getCartItems().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", "Cart is empty!")
            );
        }

        var user = authService.getCurrentUser();
        var order = Order.fromCart(cart, user);

        orderRepository.save(order);
        cartService.clearCart(cart.getId());

        return ResponseEntity.status(HttpStatus.OK).body(new CheckoutResponse(order.getId()));
    }
}

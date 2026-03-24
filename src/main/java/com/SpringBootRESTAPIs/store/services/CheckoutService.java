package com.SpringBootRESTAPIs.store.services;

import com.SpringBootRESTAPIs.store.dtos.CheckoutRequest;
import com.SpringBootRESTAPIs.store.dtos.CheckoutResponse;
import com.SpringBootRESTAPIs.store.entities.Order;
import com.SpringBootRESTAPIs.store.exceptions.CartEmptyException;
import com.SpringBootRESTAPIs.store.exceptions.CartNotFoundException;
import com.SpringBootRESTAPIs.store.repositories.CartRepository;
import com.SpringBootRESTAPIs.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;

    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart =  cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if(cart == null){
            throw new CartNotFoundException();
        }

        if(cart.isEmpty()){
            throw new CartEmptyException();
        }

        var user = authService.getCurrentUser();
        var order = Order.fromCart(cart, user);

        orderRepository.save(order);
        cartService.clearCart(cart.getId());

        return new CheckoutResponse(order.getId());
    }


}

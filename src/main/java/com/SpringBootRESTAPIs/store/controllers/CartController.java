package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.CartDto;
import com.SpringBootRESTAPIs.store.entities.Cart;
import com.SpringBootRESTAPIs.store.mappers.CartMapper;
import com.SpringBootRESTAPIs.store.repositories.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder
    ){
        Cart cart = new Cart();
        cartRepository.save(cart);
        var cartDto = cartMapper.toDto(cart);

        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cart.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(cartDto); // this is one way to send created(201) status without the location header uri.
    }
}

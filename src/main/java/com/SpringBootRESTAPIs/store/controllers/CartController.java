package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.AddItemToCartRequest;
import com.SpringBootRESTAPIs.store.dtos.CartDto;
import com.SpringBootRESTAPIs.store.dtos.CartItemDto;
import com.SpringBootRESTAPIs.store.entities.Cart;
import com.SpringBootRESTAPIs.store.entities.CartItem;
import com.SpringBootRESTAPIs.store.mappers.CartMapper;
import com.SpringBootRESTAPIs.store.repositories.CartRepository;
import com.SpringBootRESTAPIs.store.repositories.ProductRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

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

    @PostMapping("/{id}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID id,
            @Valid @RequestBody AddItemToCartRequest request
    ){
        var cart = cartRepository.findById(id).orElse(null);
        if(cart == null)
            return ResponseEntity.notFound().build();

        var product = productRepository.findById(request.getProductId()).orElse(null);
        if(product == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        // business logic to add the item to the cart
        var cartItem = cart.getCartItems().stream().filter(item -> item.getProduct().getId().equals(request.getProductId())).findFirst().orElse(null);
        if(cartItem == null){
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cart.getCartItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1); // cart item already exists, so we just increase the quantity by 1. and this cartItem will be automatically get updated in the cart object's cartItems set because it's an object reference (catItem with a reference in cartItems object).
        }

        cartRepository.save(cart); // this will also save the cartItem because of the cascade settings in the Cart entity. (considering cart as root aggregate based on DDD principles (cartItem is a child entity of cart and cannot exist without a cart, so we can cascade the save operation from cart to cartItem))

        var cartItemDto = cartMapper.toDto(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto); // here we didn't set the uri location for simplicity.
    }
}

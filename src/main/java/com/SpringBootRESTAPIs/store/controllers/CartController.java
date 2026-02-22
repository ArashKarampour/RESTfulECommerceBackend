package com.SpringBootRESTAPIs.store.controllers;

import com.SpringBootRESTAPIs.store.dtos.AddItemToCartRequest;
import com.SpringBootRESTAPIs.store.dtos.CartDto;
import com.SpringBootRESTAPIs.store.dtos.CartItemDto;
import com.SpringBootRESTAPIs.store.dtos.UpdateCartItemRequest;
import com.SpringBootRESTAPIs.store.entities.Cart;
import com.SpringBootRESTAPIs.store.entities.CartItem;
import com.SpringBootRESTAPIs.store.mappers.CartMapper;
import com.SpringBootRESTAPIs.store.repositories.CartRepository;
import com.SpringBootRESTAPIs.store.repositories.ProductRepository;
import com.SpringBootRESTAPIs.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
            UriComponentsBuilder uriBuilder
    ){
//        Cart cart = new Cart();
//        cartRepository.save(cart);
//        var cartDto = cartMapper.toDto(cart);
        var cartDto = cartService.createCart();

        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(cartDto); // this is one way to send created(201) status without the location header uri.
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID id,
            @Valid @RequestBody AddItemToCartRequest request
    ){
        var cartItemDto = cartService.addToCart(id, request.getProductId());
//        var cart = cartRepository.getCartWithItems(id).orElse(null);
//        if(cart == null)
//            return ResponseEntity.notFound().build();
//
//        var product = productRepository.findById(request.getProductId()).orElse(null);
//        if(product == null)
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//
//        // business logic to add the item to the cart (moved to Cart entity class based on Information Expert Principle in OOP and DDD principles, because the Cart entity is the one that has the information about the cart items and how to add them, so we delegate the responsibility of adding an item to the cart to the Cart entity class: see addItem method in Cart entity class)
//        var cartItem = cart.addItem(product);
//
//        cartRepository.save(cart); // this will also save the cartItem because of the cascade settings in the Cart entity. (considering cart as root aggregate based on DDD principles (cartItem is a child entity of cart and cannot exist without a cart, so we can cascade the save operation from cart to cartItem))
//
//        var cartItemDto = cartMapper.toDto(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto); // here we didn't set the uri location for simplicity.
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable UUID cartId){
//        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
//        if(cart == null)
//            return ResponseEntity.notFound().build();
//
//        return ResponseEntity.ok(cartMapper.toDto(cart));
        var cartDto = cartService.getCart(cartId);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ){
//        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
//        if(cart == null)
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    Map.of("error", "Cart was not found!")
//            );
//
//        var cartItem = cart.getItem(productId);
//
//        if(cartItem == null)
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    Map.of("error", "Product was not found in the Cart!")
//            );
//
//        cartItem.setQuantity(request.getQuantity());
//        cartRepository.save(cart); // this will also save the cartItem because of the cascade settings
//
//        return ResponseEntity.ok(cartMapper.toDto(cartItem));
        var cartItemDto = cartService.updateCartItem(cartId, productId, request.getQuantity());
        return ResponseEntity.ok(cartItemDto);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> deleteCartItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId
    ) {
//        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
//        if (cart == null)
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    Map.of("error", "Cart was not found!")
//            );
//        // we will not check if the product exists in the cart or not, because the removeItem method in the Cart entity class will handle that, and if the product does not exist in the cart it will simply do nothing, so we can just call the removeItem method without checking if the product exists in the cart or not.
//        cart.removeItem(productId);
//        cartRepository.save(cart);
        cartService.removeCartItem(cartId, productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<?> clearCart(
            @PathVariable UUID cartId
    ){
//        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
//        if (cart == null)
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    Map.of("error", "Cart was not found!")
//            );
//
//        cart.clearCart();
//        cartRepository.save(cart);
        cartService.clearCart(cartId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package com.SpringBootRESTAPIs.store.services;

import com.SpringBootRESTAPIs.store.dtos.CartDto;
import com.SpringBootRESTAPIs.store.dtos.CartItemDto;
import com.SpringBootRESTAPIs.store.entities.Cart;
import com.SpringBootRESTAPIs.store.exceptions.CartNotFoundException;
import com.SpringBootRESTAPIs.store.exceptions.ProductNotFoundException;
import com.SpringBootRESTAPIs.store.mappers.CartMapper;
import com.SpringBootRESTAPIs.store.repositories.CartRepository;
import com.SpringBootRESTAPIs.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    public CartDto createCart() {
        Cart cart = new Cart();
        cartRepository.save(cart);

        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID id, Long productId) {
        var cart = cartRepository.getCartWithItems(id).orElse(null);
        if(cart == null)
            throw new CartNotFoundException();

        var product = productRepository.findById(productId).orElse(null);
        if(product == null)
            throw new ProductNotFoundException();

        // business logic to add the item to the cart (moved to Cart entity class based on Information Expert Principle in OOP and DDD principles, because the Cart entity is the one that has the information about the cart items and how to add them, so we delegate the responsibility of adding an item to the cart to the Cart entity class: see addItem method in Cart entity class)
        var cartItem = cart.addItem(product);

        cartRepository.save(cart); // this will also save the cartItem because of the cascade settings in the Cart entity. (considering cart as root aggregate based on DDD principles (cartItem is a child entity of cart and cannot exist without a cart, so we can cascade the save operation from cart to cartItem))

        return cartMapper.toDto(cartItem);
    }

    public CartDto getCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if(cart == null)
            throw new CartNotFoundException();

        return cartMapper.toDto(cart);

    }

    public CartItemDto updateCartItem(UUID cartId, Long productId, Integer quantity) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if(cart == null)
            throw new CartNotFoundException();

        var cartItem = cart.getItem(productId);

        if(cartItem == null)
            throw new ProductNotFoundException();

        cartItem.setQuantity(quantity);
        cartRepository.save(cart); // this will also save the cartItem because of the cascade settings

        return cartMapper.toDto(cartItem);
    }

    public void removeCartItem(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null)
            throw new CartNotFoundException();
        // we will not check if the product exists in the cart or not, because the removeItem method in the Cart entity class will handle that, and if the product does not exist in the cart it will simply do nothing, so we can just call the removeItem method without checking if the product exists in the cart or not.
        cart.removeItem(productId);
        cartRepository.save(cart);
    }

     public void clearCart(UUID cartId) {
         var cart = cartRepository.getCartWithItems(cartId).orElse(null);
         if (cart == null)
             throw new CartNotFoundException();

         cart.clearCart();
         cartRepository.save(cart);
     }
}

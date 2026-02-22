package com.SpringBootRESTAPIs.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_created", insertable = false, updatable = false) // Automatically set by the database so we don't insert or update it manually
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "cart", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch =  FetchType.EAGER)
    private Set<CartItem> cartItems = new LinkedHashSet<>();

    public BigDecimal getTotalPrice() {
        return cartItems.stream()
                .map(CartItem::getTotalPrice) // get the total price for each cart item
                .reduce(BigDecimal.ZERO, BigDecimal::add); // sum up all the total prices to get the total price for the cart
    }

    public CartItem getItem(Long productId){
        return this.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public CartItem addItem(Product product){
        var cartItem = this.getItem(product.getId());
        if(cartItem == null){
            cartItem = new CartItem();
            cartItem.setCart(this);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartItems.add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1); // cart item already exists, so we just increase the quantity by 1. and this cartItem will be automatically get updated in the cart object's cartItems set because it's an object reference (catItem with a reference in cartItems object).
        }
        return cartItem;
    }
}
package com.SpringBootRESTAPIs.store.mappers;

import com.SpringBootRESTAPIs.store.dtos.CartDto;
import com.SpringBootRESTAPIs.store.dtos.CartItemDto;
import com.SpringBootRESTAPIs.store.entities.Cart;
import com.SpringBootRESTAPIs.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);
    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())") // This is how we can map a field that doesn't exist in the entity but is calculated based on other fields. In this case, we are calling the getTotalPrice() method of the CartItem entity to calculate the totalPrice for that cartItem dto.
    CartItemDto toDto(CartItem cartItem);
}

package com.SpringBootRESTAPIs.store.mappers;

import com.SpringBootRESTAPIs.store.dtos.CartDto;
import com.SpringBootRESTAPIs.store.entities.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);
}

package com.SpringBootRESTAPIs.store.mappers;

import com.SpringBootRESTAPIs.store.dtos.OrderDto;
import com.SpringBootRESTAPIs.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}

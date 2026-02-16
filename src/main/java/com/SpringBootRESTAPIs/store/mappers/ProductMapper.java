package com.SpringBootRESTAPIs.store.mappers;

import com.SpringBootRESTAPIs.store.dtos.ProductDto;
import com.SpringBootRESTAPIs.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId") // this will map id of the category field from Product to categoryId of ProductDto
    ProductDto toDto(Product product);
}

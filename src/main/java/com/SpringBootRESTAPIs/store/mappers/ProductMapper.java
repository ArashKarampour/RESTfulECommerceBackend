package com.SpringBootRESTAPIs.store.mappers;

import com.SpringBootRESTAPIs.store.dtos.ProductDto;
import com.SpringBootRESTAPIs.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId") // this will map id of the category field from Product to categoryId of ProductDto
    ProductDto toDto(Product product);
    Product toEntity(ProductDto productDto);
    @Mapping(target = "id", ignore = true) // we need to ignore the id field because we don't want to update it, we want to keep the same id of the product, and we will update only the other fields, so we need to ignore the id field in the mapping.
    void update(ProductDto productDto, @MappingTarget Product product);
}

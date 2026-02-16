package com.SpringBootRESTAPIs.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigDecimal;

@Data // instead of getter, setter, toString, equals and hashCode. just for DTOs, not for entities.
//@Getter
//@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Byte categoryId;
}

package com.SpringBootRESTAPIs.store.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemToCartRequest {
    @NotNull
    private Long productId; // we could also add a quantity field here if we want to allow adding multiple quantities of the same product to the cart in one request.
}

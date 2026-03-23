package com.SpringBootRESTAPIs.store.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CheckoutRequest {

//    @NotBlank(message = "Cart ID is required")
    private UUID cartId;
}

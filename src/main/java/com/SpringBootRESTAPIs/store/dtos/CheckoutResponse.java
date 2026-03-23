package com.SpringBootRESTAPIs.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;


@Data
public class CheckoutResponse {
    private Long orderId;

    public CheckoutResponse(Long orderId) {
        this.orderId = orderId;
    }
}

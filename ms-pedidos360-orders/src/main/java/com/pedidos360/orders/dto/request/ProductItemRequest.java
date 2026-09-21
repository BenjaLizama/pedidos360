package com.pedidos360.orders.dto.request;

import lombok.Builder;

@Builder
public record ProductItemRequest(
        String productId,
        Integer quantity
) {
}

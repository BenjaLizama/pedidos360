package com.pedidos360.orders.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record StockOperationRequest(
        List<ProductItemRequest> items
) {
}

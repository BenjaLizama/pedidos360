package com.pedidos360.orders.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        String productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}

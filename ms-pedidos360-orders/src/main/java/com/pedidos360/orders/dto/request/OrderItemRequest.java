package com.pedidos360.orders.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderItemRequest(
        @NotBlank(message = "El ID del producto es obligatorio")
        String productId,

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer quantity,

        @Min(value = 0, message = "El precio no puede ser negativo")
        java.math.BigDecimal unitPrice
) {
}

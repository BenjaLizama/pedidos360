package com.pedidos360.catalog.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProductItemRequest(
        @NotBlank(message = "El ID del producto es obligatorio")
        String productId,

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer quantity
) {
}

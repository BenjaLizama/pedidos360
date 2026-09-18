package com.pedidos360.catalog.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        String name,
        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        BigDecimal price,

        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        String categoryId
) {
}

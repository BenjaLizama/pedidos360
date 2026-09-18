package com.pedidos360.catalog.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "El nombre es obligatorio.")
        String name,

        String description,

        @NotNull(message = "El precio es obligatorio.")
        @Positive(message = "El precio debe ser mayor a cero.")
        BigDecimal price,

        @NotNull(message = "El stock es obligatorio.")
        @Min(value = 0, message = "El stock no puede ser negativo.")
        Integer stock,

        @NotBlank(message = "El ID de la categoría es obligatorio.")
        String categoryId
) {
}

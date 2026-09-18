package com.pedidos360.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio.")
        String name,

        String description
) {
}

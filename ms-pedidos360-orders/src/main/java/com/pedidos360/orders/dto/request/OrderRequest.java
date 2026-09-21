package com.pedidos360.orders.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderRequest(
        @NotBlank(message = "El ID del cliente es obligatorio")
        String clientId,

        @NotEmpty(message = "La orden debe contener al menos un ítem")
        List<@Valid OrderItemRequest> items
) {
}

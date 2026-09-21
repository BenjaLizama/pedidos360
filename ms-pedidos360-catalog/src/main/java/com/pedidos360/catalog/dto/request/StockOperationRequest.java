package com.pedidos360.catalog.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record StockOperationRequest(
        @NotEmpty(message = "La lista de productos no puede estar vacía")
        List<@Valid ProductItemRequest> items
) {
}

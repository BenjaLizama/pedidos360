package com.pedidos360.orders.dto.request;

import com.pedidos360.orders.enums.OrderStateEnum;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        OrderStateEnum status
) {
}

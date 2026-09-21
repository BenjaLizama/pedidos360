package com.pedidos360.orders.dto.response;

import lombok.Builder;

@Builder
public record StandardResponse<T>(
        Integer status,
        String message,
        T data
) {

    public static <T> StandardResponse<T> ok(String message, T data) {
        return StandardResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> StandardResponse<T> created(String message, T data) {
        return StandardResponse.<T>builder()
                .status(201)
                .message(message)
                .data(data)
                .build();
    }
}

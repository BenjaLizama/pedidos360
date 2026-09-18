package com.pedidos360.catalog.dto.response;

import com.pedidos360.catalog.document.CategorySnapshot;

import java.math.BigDecimal;

public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        CategorySnapshot category
) {
}

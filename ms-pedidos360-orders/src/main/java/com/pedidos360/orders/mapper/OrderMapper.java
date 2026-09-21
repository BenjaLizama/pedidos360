package com.pedidos360.orders.mapper;

import com.pedidos360.orders.dto.response.OrderItemResponse;
import com.pedidos360.orders.dto.response.OrderResponse;
import com.pedidos360.orders.entity.OrderEntity;
import com.pedidos360.orders.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(OrderEntity entity);

    @Mapping(target = "subtotal", expression = "java(entity.getUnitPrice().multiply(java.math.BigDecimal.valueOf(entity.getQuantity())))")
    OrderItemResponse toItemResponse(OrderItemEntity entity);
}

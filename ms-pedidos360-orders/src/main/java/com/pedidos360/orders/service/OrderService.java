package com.pedidos360.orders.service;

import com.pedidos360.orders.client.CatalogClient;
import com.pedidos360.orders.dto.request.OrderRequest;
import com.pedidos360.orders.dto.request.ProductItemRequest;
import com.pedidos360.orders.dto.request.StockOperationRequest;
import com.pedidos360.orders.dto.response.OrderResponse;
import com.pedidos360.orders.entity.OrderEntity;
import com.pedidos360.orders.entity.OrderItemEntity;
import com.pedidos360.orders.enums.OrderStateEnum;
import com.pedidos360.orders.exception.OrderNotFoundException;
import com.pedidos360.orders.mapper.OrderMapper;
import com.pedidos360.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final OrderMapper orderMapper;

    /**
     * Crea una nueva orden de compra, coordinando la validación y el descuento de stock.
     *
     * Este método es transaccional. Primero se comunica de forma síncrona con el
     * microservicio de Catálogo mediante OpenFeign para asegurar que hay inventario
     * y descontarlo. Si el catálogo rechaza la petición (ej. por falta de stock)
     * o falla la comunicación, la excepción interrumpe el flujo, la transacción
     * se revierte automáticamente y la orden no se guarda en la base de datos.
     *
     * @param request Objeto que contiene el ID del cliente y los productos que desea comprar.
     * @return OrderResponse con los detalles de la orden persistida, los subtotales y el total final.
     * @throws feign.FeignException si el microservicio de Catálogo devuelve un error HTTP.
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        List<ProductItemRequest> stockItems = request.items().stream()
                .map(item -> ProductItemRequest.builder()
                        .productId(item.productId())
                        .quantity(item.quantity())
                        .build())
                .toList();

        StockOperationRequest stockRequest = StockOperationRequest.builder()
                .items(stockItems)
                .build();

        catalogClient.validateStock(stockRequest);
        catalogClient.decreaseStock(stockRequest);

        BigDecimal totalAmount = request.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = OrderEntity.builder()
                .clientId(request.clientId())
                .orderState(OrderStateEnum.CREADO)
                .total(totalAmount)
                .build();

        request.items().forEach(itemDto -> {
            OrderItemEntity item = OrderItemEntity.builder()
                    .productId(itemDto.productId())
                    .quantity(itemDto.quantity())
                    .unitPrice(itemDto.unitPrice())
                    .build();
            order.addItem(item);
        });

        OrderEntity savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Busca una orden por su ID exacto.
     * Si no existe, lanza OrderNotFoundException que será capturada por el GlobalExceptionHandler (404).
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException("No se encontró la orden con ID: " + id));
    }

    /**
     * Recupera todas las órdenes asociadas a un cliente específico,
     * ordenadas desde la más reciente a la más antigua.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByClient(String clientId) {
        return orderRepository.findAllByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Recupera absolutamente todas las órdenes del sistema.
     * Útil para perfiles administradores o reportes generales.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    /**
     * Actualiza el estado de una orden existente (Ej. de CREADO a ACEPTADO).
     */
    @Transactional
    public OrderResponse updateOrderStatus(java.util.UUID id, OrderStateEnum newStatus) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("No se encontró la orden con ID: " + id));

        order.setOrderState(newStatus);

        // No es estrictamente necesario llamar a save() gracias a @Transactional,
        // pero es una buena práctica de legibilidad.
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    /**
     * Orquesta la búsqueda de órdenes.
     * Si recibe un customerId, filtra por ese cliente. Si es nulo o vacío, devuelve todas.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders(String customerId) {
        if (customerId != null && !customerId.isBlank()) {
            return orderRepository.findAllByClientIdOrderByCreatedAtDesc(customerId)
                    .stream()
                    .map(orderMapper::toResponse)
                    .toList();
        }

        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }
}

package com.pedidos360.orders.controller;

import com.pedidos360.orders.dto.request.OrderRequest;
import com.pedidos360.orders.dto.request.OrderStatusUpdateRequest;
import com.pedidos360.orders.dto.response.OrderResponse;
import com.pedidos360.orders.dto.response.StandardResponse;
import com.pedidos360.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * [POST] /api/v1/orders
     * Crea un nuevo pedido a partir de los ítems seleccionados y el cliente autenticado.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.created("Orden creada exitosamente", response));
    }

    /**
     * [GET] /api/v1/orders
     * [GET] /api/v1/orders?customerId={id}
     * Lista las órdenes registradas en el sistema. Delega al servicio la lógica
     * de decidir si filtra por cliente (si se envía el parámetro) o si devuelve todas.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<List<OrderResponse>>> getOrders(
            @RequestParam(required = false) String customerId) {

        List<OrderResponse> response = orderService.getOrders(customerId);

        return ResponseEntity.ok(StandardResponse.ok("Órdenes recuperadas", response));
    }

    /**
     * [GET] /api/v1/orders/{id}
     * Obtiene el detalle completo de un pedido específico por su ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(StandardResponse.ok("Orden recuperada exitosamente", response));
    }

    /**
     * [PUT] /api/v1/orders/{id}/status
     * Actualiza el estado del pedido (ej. de CREADO a ACEPTADO o DESPACHADO).
     *
     * @param id El identificador único de la orden.
     * @param request JSON con el nuevo estado (status) a aplicar.
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        OrderResponse response = orderService.updateOrderStatus(id, request.status());
        return ResponseEntity.ok(StandardResponse.ok("Estado de la orden actualizado", response));
    }
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.ok(StandardResponse.ok("Todas las órdenes recuperadas exitosamente", response));
    }
}
package bff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class BffOrderController {

    private final RestClient restClient;

    @Value("${services.orders.url}")
    private String ordersUrl;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Object> listOrders(
            @RequestParam(required = false) String customerId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {

        // Si no se especifica customerId, extrae el identificador del token JWT
        String targetCustomerId = customerId;
        if (targetCustomerId == null && jwt != null) {
            targetCustomerId = jwt.getClaimAsString("username") != null
                    ? jwt.getClaimAsString("username")
                    : jwt.getSubject();
        }

        String uri = (targetCustomerId != null && !targetCustomerId.isBlank())
                ? ordersUrl + "/api/v1/orders?customerId=" + targetCustomerId
                : ordersUrl + "/api/v1/orders";

        return restClient.get()
                .uri(uri)
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Object> getOrderById(
            @PathVariable String id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return restClient.get()
                .uri(ordersUrl + "/api/v1/orders/" + id)
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Object> createOrder(
            @RequestBody Object body,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return restClient.post()
                .uri(ordersUrl + "/api/v1/orders")
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Object> updateStatus(
            @PathVariable String id,
            @RequestBody Object body,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return restClient.put()
                .uri(ordersUrl + "/api/v1/orders/" + id + "/status")
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }
    /**
     * [GET] /api/v1/orders/all
     * Endpoint del BFF para que Operadores y Administradores obtengan todas las órdenes.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Object> getAllOrders(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {

        return restClient.get()
                .uri(ordersUrl + "/api/v1/orders/all")
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .retrieve()
                .toEntity(Object.class);
    }
}
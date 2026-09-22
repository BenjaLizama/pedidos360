package bff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> listOrders(@RequestParam(required = false) String customerId) {
        String uri = customerId != null
                ? ordersUrl + "/api/v1/orders?customerId=" + customerId
                : ordersUrl + "/api/v1/orders";

        return restClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable Long id) {
        return restClient.get()
                .uri(ordersUrl + "/api/v1/orders/" + id)
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody Object body, @AuthenticationPrincipal Jwt jwt) {
        // Reenvía la creación hacia ms-pedidos360-orders
        return restClient.post()
                .uri(ordersUrl + "/api/v1/orders")
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Object> updateStatus(@PathVariable Long id, @RequestBody Object body) {
        return restClient.put()
                .uri(ordersUrl + "/api/v1/orders/" + id + "/status")
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }
}
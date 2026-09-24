package bff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class BffCatalogController {

    private final RestClient restClient;

    @Value("${services.catalog.url}")
    private String catalogUrl;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Object> listProducts(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return restClient.get()
                .uri(catalogUrl + "/api/v1/products")
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Object> getProductById(
            @PathVariable String id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return restClient.get()
                .uri(catalogUrl + "/api/v1/products/" + id)
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Object> createProduct(
            @RequestBody Object body,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return restClient.post()
                .uri(catalogUrl + "/api/v1/products")
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Object> updateProduct(
            @PathVariable String id,
            @RequestBody Object body,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return restClient.put()
                .uri(catalogUrl + "/api/v1/products/" + id)
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return restClient.delete()
                .uri(catalogUrl + "/api/v1/products/" + id)
                .headers(h -> { if (authHeader != null) h.set(HttpHeaders.AUTHORIZATION, authHeader); })
                .retrieve()
                .toBodilessEntity();
    }
}
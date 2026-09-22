package bff.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> listProducts() {
        return restClient.get()
                .uri(catalogUrl + "/api/v1/products")
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        return restClient.get()
                .uri(catalogUrl + "/api/v1/products/" + id)
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody Object body) {
        return restClient.post()
                .uri(catalogUrl + "/api/v1/products")
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody Object body) {
        return restClient.put()
                .uri(catalogUrl + "/api/v1/products/" + id)
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return restClient.delete()
                .uri(catalogUrl + "/api/v1/products/" + id)
                .retrieve()
                .toBodilessEntity();
    }
}
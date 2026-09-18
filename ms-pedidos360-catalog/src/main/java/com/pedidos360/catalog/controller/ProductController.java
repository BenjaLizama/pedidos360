package com.pedidos360.catalog.controller;

import com.pedidos360.catalog.dto.request.ProductRequest;
import com.pedidos360.catalog.dto.response.ProductResponse;
import com.pedidos360.catalog.dto.response.StandardResponse;
import com.pedidos360.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Registra un nuevo producto en el catálogo (destinado a administradores).
     * Los datos de entrada son validados automáticamente por Spring (@Valid).
     *
     * @param request Objeto JSON con los datos obligatorios del producto (nombre, precio, stock, etc.).
     * @return ResponseEntity con los datos del producto guardado y código HTTP 201 (Created).
     */
    @PostMapping
    public ResponseEntity<StandardResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.created("Producto registrado exitosamente", product));
    }
}

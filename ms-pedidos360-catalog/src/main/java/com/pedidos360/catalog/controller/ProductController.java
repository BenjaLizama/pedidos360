package com.pedidos360.catalog.controller;

import com.pedidos360.catalog.dto.request.ProductRequest;
import com.pedidos360.catalog.dto.request.ProductUpdateRequest;
import com.pedidos360.catalog.dto.request.StockOperationRequest;
import com.pedidos360.catalog.dto.response.ProductResponse;
import com.pedidos360.catalog.dto.response.StandardResponse;
import com.pedidos360.catalog.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Lista todos los productos disponibles y activos en el catálogo.
     *
     * @return ResponseEntity con StandardResponse que contiene un array de productos y código HTTP 200.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<List<ProductResponse>>> getAllProducts() {

        List<ProductResponse> products = productService.getAllProducts();

        return ResponseEntity.ok(StandardResponse.ok("Listado de productos recuperado", products));
    }

    /**
     * Registra un nuevo producto en el catálogo (destinado a administradores).
     * Los datos de entrada son validados automáticamente por Spring (@Valid).
     *
     * @param request Objeto JSON con los datos obligatorios del producto (nombre, precio, stock, etc.).
     * @return ResponseEntity con los datos del producto guardado y código HTTP 201 (Created).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.created("Producto registrado exitosamente", product));
    }

    /**
     * Consulta la información detallada de un producto activo.
     *
     * @param id Identificador del producto a buscar.
     * @return ResponseEntity con StandardResponse y código HTTP 200 (OK).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<ProductResponse>> getProductById(@PathVariable String id) {

        ProductResponse product = productService.getProductById(id);

        return ResponseEntity.ok(StandardResponse.ok("Producto encontrado", product));
    }

    /**
     * Actualiza la información de un producto existente.
     *
     * @param id Identificador del producto a actualizar.
     * @param request Nuevos datos del producto validados por Spring.
     * @return ResponseEntity con StandardResponse y código HTTP 200 (OK).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<ProductResponse>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductUpdateRequest request) {

        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(StandardResponse.ok("Producto actualizado exitosamente", product));
    }

    /**
     * Realiza el borrado lógico del producto.
     *
     * @param id Identificador del producto a eliminar.
     * @return ResponseEntity sin cuerpo y código HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Valida la disponibilidad y el stock antes de confirmar una orden.
     */
    @PostMapping("/validate-stock")
    @PreAuthorize("hasAnyRole('CLIENTE', 'OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<Void>> validateStock(@Valid @RequestBody StockOperationRequest request) {
        productService.validateStock(request);
        return ResponseEntity.ok(StandardResponse.ok("Stock validado exitosamente. Hay disponibilidad.", null));
    }

    /**
     * Descuenta el stock del inventario cuando la orden es aceptada.
     */
    @PutMapping("/decrease-stock")
    @PreAuthorize("hasAnyRole('OPERADOR', 'ADMINISTRADOR')")
    public ResponseEntity<StandardResponse<Void>> decreaseStock(@Valid @RequestBody StockOperationRequest request) {
        productService.decreaseStock(request);
        return ResponseEntity.ok(StandardResponse.ok("Inventario actualizado y descontado exitosamente.", null));
    }
}
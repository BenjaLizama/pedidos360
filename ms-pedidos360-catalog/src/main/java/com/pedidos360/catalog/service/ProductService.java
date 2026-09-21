package com.pedidos360.catalog.service;

import com.pedidos360.catalog.document.CategoryEntity;
import com.pedidos360.catalog.document.CategorySnapshot;
import com.pedidos360.catalog.document.ProductEntity;
import com.pedidos360.catalog.dto.request.ProductItemRequest;
import com.pedidos360.catalog.dto.request.ProductRequest;
import com.pedidos360.catalog.dto.request.ProductUpdateRequest;
import com.pedidos360.catalog.dto.request.StockOperationRequest;
import com.pedidos360.catalog.dto.response.ProductResponse;
import com.pedidos360.catalog.exception.InsufficientStockException;
import com.pedidos360.catalog.exception.ResourceNotFoundException;
import com.pedidos360.catalog.mapper.ProductMapper;
import com.pedidos360.catalog.repository.CategoryRepository;
import com.pedidos360.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    /**
     * Lista todos los productos disponibles y activos en el catálogo.
     * Filtra automáticamente los productos que han sufrido un borrado lógico.
     *
     * @return Lista de ProductResponse con los datos de los productos activos.
     */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllByIsActiveTrue().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    /**
     * Contiene la lógica de negocio para crear un producto.
     * Valida que la categoría asociada exista y aplica el patrón de
     * Referencia Extendida incrustando un CategorySnapshot ligero en el documento.
     *
     * @param request DTO con la información del nuevo producto.
     * @return ProductResponseDTO con el producto persistido y su ID generado.
     * @throws ResourceNotFoundException Si el 'categoryId' proporcionado no existe en la BD.
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        CategoryEntity categoryEntity = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("La categoría con ID " + request.categoryId() + " no existe."));

        ProductEntity productEntity = productMapper.toEntity(request);

        productEntity.setCategory(new CategorySnapshot(categoryEntity.getId(), categoryEntity.getName()));

        ProductEntity savedProduct = productRepository.save(productEntity);
        return productMapper.toResponse(savedProduct);
    }

    /**
     * Consulta la información detallada de un producto específico.
     *
     * @param id Identificador único del producto en MongoDB.
     * @return ProductResponseDTO con los datos limpios del producto.
     * @throws ResourceNotFoundException Si el producto no se encuentra, lo que dispara un HTTP 404.
     */
    public ProductResponse getProductById(String id) {
        ProductEntity product = productRepository.findByIdAndIsActiveTrue(id.trim())
                .orElseThrow(() -> new ResourceNotFoundException("El producto con ID " + id + " no existe."));

        return productMapper.toResponse(product);
    }

    /**
     * Actualiza la información de un producto existente.
     * Revisa si la categoría fue modificada para actualizar el Snapshot.
     *
     * @param id Identificador del producto a actualizar.
     * @param request DTO con los nuevos datos.
     * @return ProductResponse con el producto actualizado.
     * @throws ResourceNotFoundException Si el producto o la nueva categoría no existen.
     */
    @Transactional
    public ProductResponse updateProduct(String id, ProductUpdateRequest request) {
        ProductEntity existingProduct = productRepository.findByIdAndIsActiveTrue(id.trim())
                .orElseThrow(() -> new ResourceNotFoundException("El producto con ID " + id + " no existe."));

        if (request.categoryId() != null && !existingProduct.getCategory().id().equals(request.categoryId())) {
            CategoryEntity newCategory = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("La categoría con ID " + request.categoryId() + " no existe."));
            existingProduct.setCategory(new CategorySnapshot(newCategory.getId(), newCategory.getName()));
        }

        productMapper.updateEntityFromRequest(request, existingProduct);

        ProductEntity updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponse(updatedProduct);
    }

    /**
     * Realiza el borrado lógico de un producto.
     * Cambia el estado isActive a false y registra la fecha de eliminación.
     *
     * @param id Identificador del producto a eliminar.
     * @throws ResourceNotFoundException Si el producto no existe o ya está inactivo.
     */
    @Transactional
    public void deleteProduct(String id) {
        ProductEntity product = productRepository.findByIdAndIsActiveTrue(id.trim())
                .orElseThrow(() -> new ResourceNotFoundException("El producto con ID " + id + " no existe."));

        product.setActive(false);
        product.setDeletedAt(LocalDateTime.now());
        // El campo 'deletedBy' se llenará automáticamente cuando implementemos Spring Security

        productRepository.save(product);
    }

    /**
     * Valida que todos los productos solicitados tengan stock suficiente.
     * Acumula los errores para reportar todos los productos faltantes en una sola respuesta.
     */
    @Transactional(readOnly = true)
    public void validateStock(StockOperationRequest request) {
        List<String> errorMessages = new ArrayList<>();

        for (ProductItemRequest item : request.items()) {
            ProductEntity product = productRepository.findByIdAndIsActiveTrue(item.productId().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("El producto con ID " + item.productId() + " no existe."));

            if (product.getStock() < item.quantity()) {
                errorMessages.add(String.format("'%s' (Solicitado: %d, Disponible: %d)",
                        product.getName(), item.quantity(), product.getStock()));
            }
        }

        // Si la lista de errores no está vacía, lanzamos la excepción con todos los detalles unidos
        if (!errorMessages.isEmpty()) {
            String combinedMessage = "Stock insuficiente en los siguientes productos: " +
                    String.join(" | ", errorMessages);
            throw new InsufficientStockException(combinedMessage);
        }
    }

    /**
     * Descuenta el stock del inventario para una lista de productos.
     */
    @Transactional
    public void decreaseStock(StockOperationRequest request) {
        // Primero validamos para no dejar datos a medias si alguno falla
        validateStock(request);

        // Si pasa la validación, descontamos
        for (ProductItemRequest item : request.items()) {
            ProductEntity product = productRepository.findByIdAndIsActiveTrue(item.productId().trim()).get();
            product.setStock(product.getStock() - item.quantity());
            productRepository.save(product);
        }
    }
}

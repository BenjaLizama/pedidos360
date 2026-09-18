package com.pedidos360.catalog.service;

import com.pedidos360.catalog.document.CategoryEntity;
import com.pedidos360.catalog.document.CategorySnapshot;
import com.pedidos360.catalog.document.ProductEntity;
import com.pedidos360.catalog.dto.request.ProductRequest;
import com.pedidos360.catalog.dto.response.ProductResponse;
import com.pedidos360.catalog.exception.ResourceNotFoundException;
import com.pedidos360.catalog.mapper.ProductMapper;
import com.pedidos360.catalog.repository.CategoryRepository;
import com.pedidos360.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

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
}

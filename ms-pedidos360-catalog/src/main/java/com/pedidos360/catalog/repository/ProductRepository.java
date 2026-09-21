package com.pedidos360.catalog.repository;

import com.pedidos360.catalog.document.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    List<ProductEntity> findAllByIsActiveTrue();
    Optional<ProductEntity> findByIdAndIsActiveTrue(String id);
    Optional<ProductEntity> findByName(String name);
}

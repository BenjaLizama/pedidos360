package com.pedidos360.catalog.repository;

import com.pedidos360.catalog.document.CategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<CategoryEntity, String> {
}

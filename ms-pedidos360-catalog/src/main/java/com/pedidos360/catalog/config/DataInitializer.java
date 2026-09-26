package com.pedidos360.catalog.config;

import com.pedidos360.catalog.document.CategoryEntity;
import com.pedidos360.catalog.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                CategoryEntity defaultCategory = CategoryEntity.builder()
                        .name("General")
                        .description("Categoría por defecto")
                        .build();

                CategoryEntity saved = categoryRepository.save(defaultCategory);
                log.info("Categoría inicial creada con ID: {}", saved.getId());
            }
        };
    }
}
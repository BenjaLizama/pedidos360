package com.pedidos360.catalog.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "products")
@Getter @Setter
public class ProductEntity extends SoftDeleteEntity {

    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;

    private CategorySnapshot category;
}

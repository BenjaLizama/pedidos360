package com.pedidos360.catalog.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "categories")
@Getter @Setter
public class CategoryEntity extends SoftDeleteEntity {

    private String name;
    private String description;
}

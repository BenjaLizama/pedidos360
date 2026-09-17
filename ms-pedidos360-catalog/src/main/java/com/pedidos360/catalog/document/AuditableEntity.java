package com.pedidos360.catalog.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public abstract class AuditableEntity {

    @Id
    private String id;

    @Version
    private Long version;

    @CreatedDate
    private LocalDate createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private String updatedBy;
}

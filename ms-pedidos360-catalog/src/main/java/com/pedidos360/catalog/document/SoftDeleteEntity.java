package com.pedidos360.catalog.document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@EqualsAndHashCode(callSuper = true)
public abstract class SoftDeleteEntity extends AuditableEntity {

    private boolean isActive = true;
    private LocalDateTime deletedAt;
    private String deletedBy;
}

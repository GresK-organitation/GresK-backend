package com.gresk.modules.rider.infrastructure.persistence.entity;

import com.gresk.modules.rider.domain.model.BacklineCategory;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BacklineItemEmbeddable {

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20, nullable = false)
    private BacklineCategory category;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "required", nullable = false)
    private boolean required;
}

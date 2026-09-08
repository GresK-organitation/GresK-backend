package com.gresk.modules.logistics.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndividualNeedEmbeddable {

    @Column(name = "need_type", nullable = false)
    private String type;

    @Column(name = "need_description", nullable = false, columnDefinition = "TEXT")
    private String description;
}

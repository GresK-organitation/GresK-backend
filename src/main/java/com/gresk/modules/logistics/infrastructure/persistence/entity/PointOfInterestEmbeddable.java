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
public class PointOfInterestEmbeddable {

    @Column(name = "poi_name", nullable = false)
    private String name;

    @Column(name = "poi_category", nullable = false)
    private String category;

    @Column(name = "poi_address")
    private String address;

    @Column(name = "poi_latitude")
    private Double latitude;

    @Column(name = "poi_longitude")
    private Double longitude;

    @Column(name = "poi_notes", columnDefinition = "TEXT")
    private String notes;
}

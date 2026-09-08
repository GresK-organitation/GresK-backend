package com.gresk.modules.venue.domain.model.valueobject;

import com.gresk.shared.domain.valueobject.AssetId;

import java.time.LocalDate;

/** Plano de evacuación vigente y el aforo que certifica, requerido por protección civil. */
public record EvacuationPlan(AssetId documentAsset, int certifiedCapacity, LocalDate lastReviewedAt, String reviewedBy) {

    public EvacuationPlan {
        if (documentAsset == null || documentAsset.isEmpty()) {
            throw new IllegalArgumentException("EvacuationPlan documentAsset must not be empty");
        }
        if (certifiedCapacity < 1) {
            throw new IllegalArgumentException("EvacuationPlan certifiedCapacity must be at least 1");
        }
        if (lastReviewedAt == null) {
            throw new IllegalArgumentException("EvacuationPlan lastReviewedAt must not be null");
        }
    }

    public boolean coversCapacity(int requestedCapacity) {
        return certifiedCapacity >= requestedCapacity;
    }
}

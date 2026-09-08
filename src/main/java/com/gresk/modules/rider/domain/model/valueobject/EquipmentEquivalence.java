package com.gresk.modules.rider.domain.model.valueobject;

import java.time.Instant;

public record EquipmentEquivalence(
        String requestedSpec,
        String proposedAlternative,
        SubstitutionStatus status,
        ProposedBy proposedBy,
        String notes,
        Instant proposedAt,
        Instant decidedAt
) {
    public enum ProposedBy { PROMOTER, ARTIST_MANAGEMENT }

    public EquipmentEquivalence {
        if (requestedSpec == null || requestedSpec.isBlank())
            throw new IllegalArgumentException("requestedSpec cannot be blank");
        if (proposedAlternative == null || proposedAlternative.isBlank())
            throw new IllegalArgumentException("proposedAlternative cannot be blank");
        if (status == null)
            throw new IllegalArgumentException("status cannot be null");
    }

    public static EquipmentEquivalence propose(String requestedSpec, String proposedAlternative,
                                                ProposedBy proposedBy, String notes) {
        return new EquipmentEquivalence(requestedSpec, proposedAlternative, SubstitutionStatus.PROPOSED,
                proposedBy, notes, Instant.now(), null);
    }

    public EquipmentEquivalence decide(SubstitutionStatus decision) {
        if (status != SubstitutionStatus.PROPOSED)
            throw new IllegalStateException("Equivalence already decided");
        if (decision == SubstitutionStatus.PROPOSED)
            throw new IllegalArgumentException("decision must be APPROVED or REJECTED");
        return new EquipmentEquivalence(requestedSpec, proposedAlternative, decision, proposedBy, notes,
                proposedAt, Instant.now());
    }
}

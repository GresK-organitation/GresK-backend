package com.gresk.modules.rider.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.rider.domain.model.valueobject.EquipmentEquivalence;
import com.gresk.modules.rider.domain.model.valueobject.SubstitutionStatus;

import java.time.Instant;
import java.util.Map;

final class RiderLineItemAttributeCodec {

    private RiderLineItemAttributeCodec() {}

    static String serializeAttributes(ObjectMapper objectMapper, Map<String, String> attributes) {
        if (attributes == null || attributes.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    static Map<String, String> deserializeAttributes(ObjectMapper objectMapper, String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }

    static EquipmentEquivalence toEquivalence(String requestedSpec, String proposedAlternative, String status,
                                               String proposedBy, String notes, Instant proposedAt, Instant decidedAt) {
        if (requestedSpec == null || status == null) return null;
        return new EquipmentEquivalence(requestedSpec, proposedAlternative, SubstitutionStatus.valueOf(status),
                EquipmentEquivalence.ProposedBy.valueOf(proposedBy), notes, proposedAt, decidedAt);
    }
}

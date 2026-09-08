package com.gresk.modules.contract.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.contract.domain.model.AuditTrailEntry;
import com.gresk.modules.contract.domain.model.AuditTrailEntryId;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.infrastructure.persistence.entity.ContractAuditTrailEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ContractAuditTrailMapper {

    private final ObjectMapper objectMapper;

    public AuditTrailEntry toDomain(ContractAuditTrailEntity e) {
        return AuditTrailEntry.reconstitute(
                AuditTrailEntryId.of(e.getId()),
                ContractId.of(e.getContractId()),
                e.getAction(),
                e.getActor(),
                e.getOccurredAt(),
                e.getIpAddress(),
                e.getUserAgent(),
                deserializeMetadata(e.getMetadataJson()),
                e.getDocumentHash()
        );
    }

    public ContractAuditTrailEntity toEntity(AuditTrailEntry entry) {
        return ContractAuditTrailEntity.builder()
                .id(entry.getId().value())
                .contractId(entry.getContractId().value())
                .action(entry.getAction())
                .actor(entry.getActor())
                .occurredAt(entry.getOccurredAt())
                .ipAddress(entry.getIpAddress())
                .userAgent(entry.getUserAgent())
                .metadataJson(serializeMetadata(entry.getMetadata()))
                .documentHash(entry.getDocumentHash())
                .build();
    }

    private String serializeMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private Map<String, Object> deserializeMetadata(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}

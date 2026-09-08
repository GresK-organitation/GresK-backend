package com.gresk.modules.contract.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.SignatureEnvelope;
import com.gresk.modules.contract.domain.model.SignatureEnvelopeId;
import com.gresk.modules.contract.domain.model.valueobject.Signer;
import com.gresk.modules.contract.infrastructure.persistence.entity.SignatureEnvelopeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SignatureEnvelopeMapper {

    private final ObjectMapper objectMapper;

    public SignatureEnvelope toDomain(SignatureEnvelopeEntity e) {
        return SignatureEnvelope.reconstitute(
                SignatureEnvelopeId.of(e.getId()),
                ContractId.of(e.getContractId()),
                e.getProvider(),
                e.getProviderEnvelopeId(),
                e.getStatus(),
                e.getDocumentHash(),
                e.getCertificateAssetId(),
                deserializeSigners(e.getSignersJson()),
                e.getSentAt(),
                e.getCompletedAt(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public SignatureEnvelopeEntity toEntity(SignatureEnvelope envelope) {
        return SignatureEnvelopeEntity.builder()
                .id(envelope.getId().value())
                .contractId(envelope.getContractId().value())
                .provider(envelope.getProvider())
                .providerEnvelopeId(envelope.getProviderEnvelopeId())
                .status(envelope.getStatus())
                .documentHash(envelope.getDocumentHash())
                .certificateAssetId(envelope.getCertificateAssetId())
                .signersJson(serializeSigners(envelope.getSigners()))
                .sentAt(envelope.getSentAt())
                .completedAt(envelope.getCompletedAt())
                .createdAt(envelope.getCreatedAt())
                .updatedAt(envelope.getUpdatedAt())
                .build();
    }

    private String serializeSigners(List<Signer> signers) {
        try {
            return objectMapper.writeValueAsString(signers != null ? signers : List.of());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<Signer> deserializeSigners(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}

package com.gresk.modules.contract.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.contract.domain.model.ContractId;
import com.gresk.modules.contract.domain.model.ContractVersion;
import com.gresk.modules.contract.domain.model.ContractVersionId;
import com.gresk.modules.contract.domain.model.valueobject.ContractClause;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.FinancialTerms;
import com.gresk.modules.contract.domain.model.valueobject.PerformanceDetails;
import com.gresk.modules.contract.infrastructure.persistence.entity.ContractVersionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractVersionMapper {

    private final ObjectMapper objectMapper;

    public ContractVersion toDomain(ContractVersionEntity e) {
        return ContractVersion.reconstitute(
                ContractVersionId.of(e.getId()),
                ContractId.of(e.getContractId()),
                e.getVersionNumber(),
                readValue(e.getPartyAJson(), ContractParty.class),
                readValue(e.getPartyBJson(), ContractParty.class),
                readValue(e.getPerformanceDetailsJson(), PerformanceDetails.class),
                readValue(e.getFinancialTermsJson(), FinancialTerms.class),
                readList(e.getClausesJson()),
                e.getChangeSummary(),
                e.getCreatedBy(),
                e.getCreatedAt(),
                e.getStatus()
        );
    }

    public ContractVersionEntity toEntity(ContractVersion version) {
        return ContractVersionEntity.builder()
                .id(version.getId().value())
                .contractId(version.getContractId().value())
                .versionNumber(version.getVersionNumber())
                .status(version.getStatus())
                .partyAJson(writeValue(version.getPartyA()))
                .partyBJson(writeValue(version.getPartyB()))
                .performanceDetailsJson(writeValue(version.getPerformanceDetails()))
                .financialTermsJson(writeValue(version.getFinancialTerms()))
                .clausesJson(writeValue(version.getClauses()))
                .changeSummary(version.getChangeSummary())
                .createdBy(version.getCreatedBy())
                .createdAt(version.getCreatedAt())
                .build();
    }

    private String writeValue(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private <T> T readValue(String json, Class<T> type) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<ContractClause> readList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ContractClause.class));
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}

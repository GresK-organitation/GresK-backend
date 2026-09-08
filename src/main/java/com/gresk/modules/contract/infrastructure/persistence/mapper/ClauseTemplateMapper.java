package com.gresk.modules.contract.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.contract.domain.model.ClauseTemplate;
import com.gresk.modules.contract.domain.model.ClauseTemplateId;
import com.gresk.modules.contract.domain.model.ContractType;
import com.gresk.modules.contract.infrastructure.persistence.entity.ClauseTemplateEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClauseTemplateMapper {

    private final ObjectMapper objectMapper;

    public ClauseTemplate toDomain(ClauseTemplateEntity e) {
        return ClauseTemplate.reconstitute(
                ClauseTemplateId.of(e.getId()),
                e.getPromoterId() != null ? PromoterId.of(e.getPromoterId()) : null,
                e.getCode(),
                e.getCategory(),
                e.getTitle(),
                e.getContentTemplate(),
                deserializeTypes(e.getApplicableTypesJson()),
                e.getJurisdictionScope(),
                e.isSystemDefault(),
                e.getVersion(),
                e.isActive()
        );
    }

    public ClauseTemplateEntity toEntity(ClauseTemplate template) {
        return ClauseTemplateEntity.builder()
                .id(template.getId().value())
                .promoterId(template.getPromoterId() != null ? template.getPromoterId().value() : null)
                .code(template.getCode())
                .category(template.getCategory())
                .title(template.getTitle())
                .contentTemplate(template.getContentTemplate())
                .applicableTypesJson(serializeTypes(template.getApplicableTypes()))
                .jurisdictionScope(template.getJurisdictionScope())
                .systemDefault(template.isSystemDefault())
                .version(template.getVersion())
                .active(template.isActive())
                .build();
    }

    private String serializeTypes(List<ContractType> types) {
        try {
            return objectMapper.writeValueAsString(types != null ? types : List.of());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<ContractType> deserializeTypes(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}

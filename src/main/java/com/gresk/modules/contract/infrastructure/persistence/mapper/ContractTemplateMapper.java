package com.gresk.modules.contract.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.contract.domain.model.ClauseTemplateId;
import com.gresk.modules.contract.domain.model.ContractTemplate;
import com.gresk.modules.contract.domain.model.ContractTemplateId;
import com.gresk.modules.contract.domain.model.valueobject.TemplateVariable;
import com.gresk.modules.contract.infrastructure.persistence.entity.ContractTemplateEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContractTemplateMapper {

    private final ObjectMapper objectMapper;

    public ContractTemplate toDomain(ContractTemplateEntity e) {
        return ContractTemplate.reconstitute(
                ContractTemplateId.of(e.getId()),
                e.getPromoterId() != null ? PromoterId.of(e.getPromoterId()) : null,
                e.getType(),
                e.getName(),
                e.getBodyMarkdown(),
                deserializeVariables(e.getVariablesJson()),
                deserializeClauseIds(e.getDefaultClauseTemplateIdsJson()),
                e.getVersion(),
                e.isActive(),
                e.getCreatedAt()
        );
    }

    public ContractTemplateEntity toEntity(ContractTemplate template) {
        return ContractTemplateEntity.builder()
                .id(template.getId().value())
                .promoterId(template.getPromoterId() != null ? template.getPromoterId().value() : null)
                .type(template.getType())
                .name(template.getName())
                .bodyMarkdown(template.getBodyMarkdown())
                .variablesJson(serializeVariables(template.getVariables()))
                .defaultClauseTemplateIdsJson(serializeClauseIds(template.getDefaultClauseTemplateIds()))
                .version(template.getVersion())
                .active(template.isActive())
                .createdAt(template.getCreatedAt())
                .build();
    }

    private String serializeVariables(List<TemplateVariable> variables) {
        try {
            return objectMapper.writeValueAsString(variables != null ? variables : List.of());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<TemplateVariable> deserializeVariables(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    private String serializeClauseIds(List<ClauseTemplateId> ids) {
        try {
            List<String> raw = ids != null ? ids.stream().map(ClauseTemplateId::toString).toList() : List.of();
            return objectMapper.writeValueAsString(raw);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<ClauseTemplateId> deserializeClauseIds(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            List<String> raw = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return raw.stream().map(s -> ClauseTemplateId.of(UUID.fromString(s))).toList();
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}

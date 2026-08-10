package com.gresk.modules.email.infrastructure.persistence.mapper;

import com.gresk.modules.email.domain.model.EmailEntityRecord;
import com.gresk.modules.email.domain.model.EmailEntityRecordId;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.infrastructure.persistence.entity.EmailEntityRecordEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailEntityRecordMapper {

    public EmailEntityRecord toDomain(EmailEntityRecordEntity e) {
        return EmailEntityRecord.reconstitute(
                EmailEntityRecordId.of(e.getId()),
                EmailMessageId.of(e.getEmailId()),
                e.getEntityType(),
                e.getEntityKey(),
                e.getEntityValue(),
                e.getNormalizedValueJson(),
                e.getConfidence(),
                e.getSourceSnippet(),
                e.isRequiresAction(),
                e.getActionedAt(),
                e.getCreatedAt()
        );
    }

    public EmailEntityRecordEntity toEntity(EmailEntityRecord r) {
        return EmailEntityRecordEntity.builder()
                .id(r.getId().value())
                .emailId(r.getEmailId().value())
                .entityType(r.getEntityType())
                .entityKey(r.getEntityKey())
                .entityValue(r.getEntityValue())
                .normalizedValueJson(r.getNormalizedValueJson())
                .confidence(r.getConfidence())
                .sourceSnippet(r.getSourceSnippet())
                .requiresAction(r.isRequiresAction())
                .actionedAt(r.getActionedAt())
                .createdAt(r.getCreatedAt())
                .build();
    }
}

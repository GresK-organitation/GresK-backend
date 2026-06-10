package com.gresk.modules.email.infrastructure.persistence.mapper;

import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.model.EmailRiderVersionId;
import com.gresk.modules.email.infrastructure.persistence.entity.EmailRiderVersionEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailRiderVersionMapper {

    public EmailRiderVersion toDomain(EmailRiderVersionEntity e) {
        return EmailRiderVersion.reconstitute(
                EmailRiderVersionId.of(e.getId()),
                e.getEventId(),
                e.getVersionNumber(),
                e.getSourceEmailId() != null ? EmailMessageId.of(e.getSourceEmailId()) : null,
                e.getRiderDataJson(),
                e.getDiffFromPrevJson(),
                e.getCreatedBy(),
                e.getNotes(),
                e.getCreatedAt()
        );
    }

    public EmailRiderVersionEntity toEntity(EmailRiderVersion v) {
        return EmailRiderVersionEntity.builder()
                .id(v.getId().value())
                .eventId(v.getEventId())
                .versionNumber(v.getVersionNumber())
                .sourceEmailId(v.getSourceEmailId() != null ? v.getSourceEmailId().value() : null)
                .riderDataJson(v.getRiderDataJson())
                .diffFromPrevJson(v.getDiffFromPrevJson())
                .createdBy(v.getCreatedBy())
                .notes(v.getNotes())
                .createdAt(v.getCreatedAt())
                .build();
    }
}

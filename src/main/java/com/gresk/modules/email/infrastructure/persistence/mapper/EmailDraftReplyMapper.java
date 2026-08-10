package com.gresk.modules.email.infrastructure.persistence.mapper;

import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.model.EmailDraftReplyId;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.email.infrastructure.persistence.entity.EmailDraftReplyEntity;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.springframework.stereotype.Component;

@Component
public class EmailDraftReplyMapper {

    public EmailDraftReply toDomain(EmailDraftReplyEntity e) {
        return EmailDraftReply.reconstitute(
                EmailDraftReplyId.of(e.getId()),
                EmailMessageId.of(e.getEmailId()),
                PromoterId.of(e.getPromoterId()),
                e.getDraftType(),
                e.getSubject(),
                e.getBody(),
                e.getEditedBody(),
                e.getStatus(),
                e.getApprovedAt(),
                e.getSentAt(),
                e.getCreatedAt()
        );
    }

    public EmailDraftReplyEntity toEntity(EmailDraftReply d) {
        return EmailDraftReplyEntity.builder()
                .id(d.getId().value())
                .emailId(d.getEmailId().value())
                .promoterId(d.getPromoterId().value())
                .draftType(d.getDraftType())
                .subject(d.getSubject())
                .body(d.getBody())
                .editedBody(d.getEditedBody())
                .status(d.getStatus())
                .approvedAt(d.getApprovedAt())
                .sentAt(d.getSentAt())
                .createdAt(d.getCreatedAt())
                .build();
    }
}

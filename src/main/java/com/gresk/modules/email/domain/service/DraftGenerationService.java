package com.gresk.modules.email.domain.service;

import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailProcessingResult;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Crea borradores de respuesta a partir del resultado del procesamiento IA.
 * El borrador queda en PENDING_REVIEW hasta que la promotora lo apruebe.
 */
@Component
public class DraftGenerationService {

    public Optional<EmailDraftReply> generateFrom(EmailMessage email, EmailProcessingResult result) {
        if (!result.hasSuggestedReply()) {
            return Optional.empty();
        }

        String subject = result.suggestedReplySubject() != null
                ? result.suggestedReplySubject()
                : replySubject(email);

        return Optional.of(EmailDraftReply.create(
                email.getId(),
                email.getPromoterId(),
                result.classification().classification().name(),
                subject,
                result.suggestedReplyBody()
        ));
    }

    private String replySubject(EmailMessage email) {
        String original = email.getSubject() != null ? email.getSubject() : "";
        return original.toLowerCase().startsWith("re:") ? original : "Re: " + original;
    }
}

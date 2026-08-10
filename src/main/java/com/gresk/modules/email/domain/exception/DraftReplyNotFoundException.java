package com.gresk.modules.email.domain.exception;

import com.gresk.modules.email.domain.model.EmailDraftReplyId;

public class DraftReplyNotFoundException extends RuntimeException {
    public DraftReplyNotFoundException(EmailDraftReplyId id) {
        super("Draft reply not found: " + id);
    }
}

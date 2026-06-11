package com.gresk.modules.email.application.dto;

import com.gresk.modules.email.domain.model.EmailDraftReply;
import com.gresk.modules.email.domain.model.EmailMessage;

/** Borrador junto al email original al que responde. */
public record DraftDetail(EmailDraftReply draft, EmailMessage originalEmail) {}

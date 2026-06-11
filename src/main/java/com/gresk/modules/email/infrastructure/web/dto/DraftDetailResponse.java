package com.gresk.modules.email.infrastructure.web.dto;

public record DraftDetailResponse(
        DraftReplyResponse draft,
        EmailDetailResponse originalEmail
) {}

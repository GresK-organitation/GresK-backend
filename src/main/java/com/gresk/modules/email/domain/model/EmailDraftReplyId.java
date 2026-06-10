package com.gresk.modules.email.domain.model;

import java.util.Objects;
import java.util.UUID;

public record EmailDraftReplyId(UUID value) {

    public EmailDraftReplyId {
        Objects.requireNonNull(value, "EmailDraftReplyId value must not be null");
    }

    public static EmailDraftReplyId generate() {
        return new EmailDraftReplyId(UUID.randomUUID());
    }

    public static EmailDraftReplyId of(String value) {
        try {
            return new EmailDraftReplyId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid EmailDraftReplyId format: " + value, e);
        }
    }

    public static EmailDraftReplyId of(UUID value) {
        return new EmailDraftReplyId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

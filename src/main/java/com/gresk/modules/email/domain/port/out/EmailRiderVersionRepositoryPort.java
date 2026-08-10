package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.email.domain.model.EmailRiderVersion;
import com.gresk.modules.email.domain.model.EmailRiderVersionId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailRiderVersionRepositoryPort {
    EmailRiderVersion           save(EmailRiderVersion version);
    Optional<EmailRiderVersion> findById(EmailRiderVersionId id);
    List<EmailRiderVersion>     findByEventId(UUID eventId);
    Optional<EmailRiderVersion> findLatestByEventId(UUID eventId);
}

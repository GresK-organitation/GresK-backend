package com.gresk.modules.email.domain.port.out;

import com.gresk.modules.email.domain.model.EmailEntityRecord;
import com.gresk.modules.email.domain.model.EmailEntityRecordId;
import com.gresk.modules.email.domain.model.EmailMessageId;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmailEntityRecordRepositoryPort {
    EmailEntityRecord           save(EmailEntityRecord record);
    List<EmailEntityRecord>     saveAll(List<EmailEntityRecord> records);
    Optional<EmailEntityRecord> findById(EmailEntityRecordId id);
    List<EmailEntityRecord>     findByEmailId(EmailMessageId emailId);
    List<EmailEntityRecord>     findByEmailIds(Collection<UUID> emailIds);
    List<EmailEntityRecord>     findPendingAction();
}

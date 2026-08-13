package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.journal.domain.exception.JournalEntryForbiddenException;
import com.gresk.modules.journal.domain.exception.JournalEntryNotFoundException;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntryId;
import com.gresk.modules.journal.domain.model.JournalMedia;
import com.gresk.modules.journal.domain.model.JournalMediaId;
import com.gresk.modules.journal.domain.model.JournalMediaType;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.port.out.VideoStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveJournalMediaUseCase {

    private final JournalEntryRepository repository;
    private final ImageStoragePort       imageStoragePort;
    private final VideoStoragePort       videoStoragePort;

    @Transactional
    public JournalEntry execute(String entryId, String userId, String mediaId) {
        JournalEntry entry = repository.findById(JournalEntryId.of(entryId))
                .orElseThrow(() -> new JournalEntryNotFoundException("Journal entry not found: " + entryId));

        if (!entry.getUserId().equals(UserId.from(userId))) {
            throw new JournalEntryForbiddenException("Journal entry does not belong to this user");
        }

        JournalMediaId id = JournalMediaId.of(mediaId);
        entry.getMedia().stream()
                .filter(m -> m.id().equals(id))
                .findFirst()
                .ifPresent(this::deleteFromStorage);

        entry.removeMedia(id);
        return repository.save(entry);
    }

    private void deleteFromStorage(JournalMedia media) {
        if (media.mediaType() == JournalMediaType.PHOTO) {
            imageStoragePort.delete(media.assetId());
        } else {
            videoStoragePort.delete(media.assetId());
        }
    }
}

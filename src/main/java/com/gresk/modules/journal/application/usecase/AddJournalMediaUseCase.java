package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.journal.domain.exception.JournalEntryForbiddenException;
import com.gresk.modules.journal.domain.exception.JournalEntryNotFoundException;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntryId;
import com.gresk.modules.journal.domain.model.JournalMedia;
import com.gresk.modules.journal.domain.model.JournalMediaType;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.port.out.VideoStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.VideoUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AddJournalMediaUseCase {

    private static final String PHOTO_FOLDER = "journal/photos";
    private static final String VIDEO_FOLDER = "journal/videos";

    private final JournalEntryRepository repository;
    private final ImageStoragePort       imageStoragePort;
    private final VideoStoragePort       videoStoragePort;

    @Transactional
    public JournalEntry execute(String entryId, String userId, JournalMediaType mediaType, MultipartFile file) {
        JournalEntry entry = repository.findById(JournalEntryId.of(entryId))
                .orElseThrow(() -> new JournalEntryNotFoundException("Journal entry not found: " + entryId));

        if (!entry.getUserId().equals(UserId.from(userId))) {
            throw new JournalEntryForbiddenException("Journal entry does not belong to this user");
        }

        int displayOrder = entry.getMedia().size();
        JournalMedia media = (mediaType == JournalMediaType.PHOTO)
                ? JournalMedia.photo(uploadPhoto(file), displayOrder)
                : uploadVideo(file, displayOrder);

        entry.addMedia(media);
        return repository.save(entry);
    }

    private AssetId uploadPhoto(MultipartFile file) {
        return imageStoragePort.upload(file, PHOTO_FOLDER);
    }

    private JournalMedia uploadVideo(MultipartFile file, int displayOrder) {
        VideoUploadResult result = videoStoragePort.upload(file, VIDEO_FOLDER);
        return JournalMedia.video(result.assetId(), displayOrder, result.durationSeconds());
    }
}

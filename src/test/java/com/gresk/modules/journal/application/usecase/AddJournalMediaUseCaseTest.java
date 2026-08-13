package com.gresk.modules.journal.application.usecase;

import com.gresk.modules.journal.domain.exception.JournalEntryForbiddenException;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntrySource;
import com.gresk.modules.journal.domain.model.JournalMediaType;
import com.gresk.modules.journal.domain.model.JournalVisibility;
import com.gresk.modules.journal.domain.model.ApproxDate;
import com.gresk.modules.journal.domain.port.out.JournalEntryRepository;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.port.out.ImageStoragePort;
import com.gresk.shared.domain.port.out.VideoStoragePort;
import com.gresk.shared.domain.valueobject.AssetId;
import com.gresk.shared.domain.valueobject.VideoUploadResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddJournalMediaUseCaseTest {

    @Mock private JournalEntryRepository repository;
    @Mock private ImageStoragePort       imageStoragePort;
    @Mock private VideoStoragePort       videoStoragePort;

    private AddJournalMediaUseCase useCase;
    private final UserId userId = UserId.of(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        useCase = new AddJournalMediaUseCase(repository, imageStoragePort, videoStoragePort);
    }

    private JournalEntry newEntry() {
        return JournalEntry.create(userId, "Radiohead", null, ApproxDate.ofYear(2019),
                null, null, null, null, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);
    }

    @Test
    void subeUnaFotoYLaAnadeALaEntrada() {
        JournalEntry entry = newEntry();
        MockMultipartFile file = new MockMultipartFile("file", "pic.jpg", "image/jpeg", new byte[]{1});
        when(repository.findById(entry.getId())).thenReturn(Optional.of(entry));
        when(imageStoragePort.upload(eq(file), any())).thenReturn(AssetId.of("journal/photos/pic1"));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JournalEntry result = useCase.execute(entry.getId().toString(), userId.toString(), JournalMediaType.PHOTO, file);

        assertEquals(1, result.getMedia().size());
    }

    @Test
    void subeUnVideoYLaAnadeConLaDuracionDevueltaPorElAdapter() {
        JournalEntry entry = newEntry();
        MockMultipartFile file = new MockMultipartFile("file", "clip.mp4", "video/mp4", new byte[]{1});
        when(repository.findById(entry.getId())).thenReturn(Optional.of(entry));
        when(videoStoragePort.upload(eq(file), any()))
                .thenReturn(new VideoUploadResult(AssetId.of("journal/videos/clip1"), 12));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        JournalEntry result = useCase.execute(entry.getId().toString(), userId.toString(), JournalMediaType.VIDEO, file);

        assertEquals(1, result.getMedia().size());
        assertEquals(12, result.getMedia().get(0).durationSeconds());
    }

    @Test
    void rechazaSiLaEntradaNoPerteneceAlUsuario() {
        JournalEntry entry = newEntry();
        MockMultipartFile file = new MockMultipartFile("file", "pic.jpg", "image/jpeg", new byte[]{1});
        when(repository.findById(entry.getId())).thenReturn(Optional.of(entry));

        String otherUserId = UserId.of(UUID.randomUUID()).toString();

        assertThrows(JournalEntryForbiddenException.class, () ->
                useCase.execute(entry.getId().toString(), otherUserId, JournalMediaType.PHOTO, file));
    }
}

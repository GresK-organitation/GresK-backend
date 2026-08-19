package com.gresk.modules.musicdna.application.usecase;

import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignalsPort;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Recalcula el ADN Musical de todos los usuarios con actividad (Review o
 * JournalEntry) en los últimos 7 días. Un fallo aislado no bloquea el resto
 * del batch — mismo criterio que IngestChroniclesUseCase.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecalculateStaleMusicDnaUseCase {

    private static final int ACTIVITY_WINDOW_DAYS = 7;

    private final MusicDnaSignalsPort          signalsPort;
    private final CalculateUserMusicDnaUseCase calculateUserMusicDnaUseCase;

    public void execute() {
        Instant since = Instant.now().minus(ACTIVITY_WINDOW_DAYS, ChronoUnit.DAYS);
        List<UserId> userIds = signalsPort.findUserIdsWithActivitySince(since);

        for (UserId userId : userIds) {
            try {
                calculateUserMusicDnaUseCase.execute(userId);
            } catch (Exception e) {
                log.error("Failed to recalculate Music DNA for user {}: {}", userId, e.getMessage());
            }
        }
    }
}

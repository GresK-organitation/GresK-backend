package com.gresk.modules.musicdna.infrastructure.event;

import com.gresk.modules.musicdna.application.port.out.MusicDnaAsyncRecalculationPort;
import com.gresk.modules.musicdna.application.usecase.CalculateUserMusicDnaUseCase;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicDnaAsyncRecalculationAdapter implements MusicDnaAsyncRecalculationPort {

    private final CalculateUserMusicDnaUseCase calculateUserMusicDnaUseCase;

    @Async
    @Override
    public void recalculateAsync(UserId userId) {
        try {
            calculateUserMusicDnaUseCase.execute(userId);
        } catch (Exception e) {
            log.error("Async Music DNA recalculation failed for user {}: {}", userId, e.getMessage());
        }
    }
}

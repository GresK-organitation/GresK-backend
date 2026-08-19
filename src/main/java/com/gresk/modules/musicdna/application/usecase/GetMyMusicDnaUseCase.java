package com.gresk.modules.musicdna.application.usecase;

import com.gresk.modules.musicdna.application.port.out.MusicDnaAsyncRecalculationPort;
import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Sirve el ADN propio con patrón "stale-while-revalidate": devuelve el dato
 * ya materializado (posiblemente desactualizado) y dispara un recálculo
 * asíncrono si tiene 30+ días, sin bloquear la respuesta.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyMusicDnaUseCase {

    private static final int STALE_AFTER_DAYS = 30;

    private final MusicDnaRepository             repository;
    private final MusicDnaAsyncRecalculationPort asyncRecalculationPort;

    public Optional<UserMusicDna> execute(UserId userId) {
        Optional<UserMusicDna> current = repository.findByUserId(userId);

        boolean stale = current
                .map(dna -> dna.getCalculatedAt().isBefore(Instant.now().minus(STALE_AFTER_DAYS, ChronoUnit.DAYS)))
                .orElse(false);

        if (stale) {
            asyncRecalculationPort.recalculateAsync(userId);
        }

        return current;
    }
}

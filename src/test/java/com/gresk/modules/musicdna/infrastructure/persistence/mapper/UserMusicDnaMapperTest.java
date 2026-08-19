package com.gresk.modules.musicdna.infrastructure.persistence.mapper;

import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import com.gresk.modules.musicdna.infrastructure.persistence.entity.UserMusicDnaEntity;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMusicDnaMapperTest {

    private final UserMusicDnaMapper mapper = new UserMusicDnaMapper();

    @Test
    void toEntityYVueltaADominioPreservaScoresYLabels() {
        UserId userId = UserId.of(UUID.randomUUID());
        MusicDnaSignals signals = new MusicDnaSignals(3, 1, 2, 1, 3, 3, 2, 2, 0,
                LocalDate.now().minusYears(1), Instant.now().minusSeconds(3600));
        UserMusicDna original = UserMusicDna.calculate(userId, signals);

        UserMusicDnaEntity entity = mapper.toEntity(original);
        UserMusicDna roundTripped = mapper.toDomain(entity);

        assertEquals(original.getUserId(), roundTripped.getUserId());
        assertEquals(original.getIntensidad().score(), roundTripped.getIntensidad().score());
        assertEquals(original.getIntensidad().label(), roundTripped.getIntensidad().label());
        assertEquals(original.getSummaryPhrase(), roundTripped.getSummaryPhrase());
        assertEquals(original.getCalculatedAt(), roundTripped.getCalculatedAt());
    }
}

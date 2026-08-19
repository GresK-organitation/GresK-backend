package com.gresk.modules.musicdna.application.usecase;

import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaRepository;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignalsPort;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalculateUserMusicDnaUseCase {

    private final MusicDnaSignalsPort signalsPort;
    private final MusicDnaRepository  repository;

    @Transactional
    public UserMusicDna execute(UserId userId) {
        MusicDnaSignals signals = signalsPort.findSignals(userId);
        UserMusicDna dna = UserMusicDna.calculate(userId, signals);
        return repository.save(dna);
    }
}

package com.gresk.modules.musicdna.domain.port.out;

import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.user.domain.model.UserId;

import java.util.Optional;

public interface MusicDnaRepository {
    UserMusicDna save(UserMusicDna dna);
    Optional<UserMusicDna> findByUserId(UserId userId);
}

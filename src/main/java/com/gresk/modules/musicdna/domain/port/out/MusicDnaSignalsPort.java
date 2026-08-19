package com.gresk.modules.musicdna.domain.port.out;

import com.gresk.modules.user.domain.model.UserId;

import java.time.Instant;
import java.util.List;

public interface MusicDnaSignalsPort {
    MusicDnaSignals findSignals(UserId userId);
    List<UserId> findUserIdsWithActivitySince(Instant since);
}

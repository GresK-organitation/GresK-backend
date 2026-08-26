package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.discovery.domain.port.out.UserGenreAffinity;
import com.gresk.modules.discovery.domain.port.out.UserGenreAffinityPort;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignalsPort;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.MusicGenre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserGenreAffinityAdapter implements UserGenreAffinityPort {

    private final MusicDnaSignalsPort musicDnaSignalsPort;

    @Override
    public UserGenreAffinity findTopGenres(UserId userId, int limit) {
        var genres = musicDnaSignalsPort.findTopGenres(userId, limit).stream()
                .map(MusicGenre::valueOf)
                .toList();
        return new UserGenreAffinity(genres);
    }
}

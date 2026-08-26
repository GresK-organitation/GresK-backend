package com.gresk.modules.discovery.domain.port.out;

import com.gresk.shared.domain.MusicGenre;

import java.util.List;

public record UserGenreAffinity(List<MusicGenre> topGenres) {
}

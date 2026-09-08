package com.gresk.modules.show.infrastructure.web.dto;

import com.gresk.shared.domain.MusicGenre;
import jakarta.validation.constraints.NotNull;

public record OpenSalesRequest(@NotNull MusicGenre genre) {}

package com.gresk.modules.show.infrastructure.web.dto;

import com.gresk.modules.show.domain.model.valueobject.LogEntryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddLogEntryRequest(
        @NotNull  LogEntryType type,
        @NotBlank String actor,
        @NotBlank String description,
        String relatedParty,
        List<String> attachmentAssetIds
) {}

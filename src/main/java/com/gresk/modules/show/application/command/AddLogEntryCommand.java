package com.gresk.modules.show.application.command;

import com.gresk.modules.show.domain.model.valueobject.LogEntryType;

import java.util.List;

public record AddLogEntryCommand(
        String showId,
        String promoterId,
        LogEntryType type,
        String actor,
        String description,
        String relatedParty,
        List<String> attachmentAssetIds
) {
}

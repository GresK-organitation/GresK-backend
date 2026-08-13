package com.gresk.modules.journal.application.dto;

import com.gresk.modules.journal.domain.model.JournalEntry;

import java.util.List;

public record BulkCreateResult(List<JournalEntry> created, List<BulkCreateFailure> failures) {}

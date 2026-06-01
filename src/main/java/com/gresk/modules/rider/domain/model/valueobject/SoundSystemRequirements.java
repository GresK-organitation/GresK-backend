package com.gresk.modules.rider.domain.model.valueobject;

public record SoundSystemRequirements(
        String consoleBrand,
        Integer consoleChannels,
        Integer monitorMixes,
        String paDescription,
        String processorNotes
) {}

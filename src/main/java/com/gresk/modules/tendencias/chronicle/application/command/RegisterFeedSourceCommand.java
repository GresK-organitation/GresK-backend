package com.gresk.modules.tendencias.chronicle.application.command;

public record RegisterFeedSourceCommand(String name, String feedUrl, String sourceUrl) {
}

package com.gresk.modules.agenda.application.command;

public record CompleteCalendarConnectionCommand(String state, String code, String provider) {
}

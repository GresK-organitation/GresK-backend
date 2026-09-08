package com.gresk.modules.logistics.application.command;

public record ChangeTourStatusCommand(String tourId, String promoterId, String targetStatus, String reason) {
}

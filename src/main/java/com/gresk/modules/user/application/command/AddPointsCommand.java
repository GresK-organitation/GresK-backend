package com.gresk.modules.user.application.command;

public record AddPointsCommand(
        String userId,
        int pointsToAdd
) {}
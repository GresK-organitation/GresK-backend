package com.gresk.modules.finance.application.command;

public record RemoveCostLineCommand(
        String planId,
        String costLineId,
        String promoterId
) {
}

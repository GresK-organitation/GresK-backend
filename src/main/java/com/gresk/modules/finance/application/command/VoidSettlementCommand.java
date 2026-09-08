package com.gresk.modules.finance.application.command;

public record VoidSettlementCommand(String settlementId, String promoterId) {
}

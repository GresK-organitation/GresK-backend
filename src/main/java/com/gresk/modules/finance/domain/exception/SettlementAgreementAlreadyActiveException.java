package com.gresk.modules.finance.domain.exception;

public class SettlementAgreementAlreadyActiveException extends RuntimeException {
    public SettlementAgreementAlreadyActiveException(String linkedContractId) {
        super("An active settlement agreement already exists for contract: " + linkedContractId);
    }
}

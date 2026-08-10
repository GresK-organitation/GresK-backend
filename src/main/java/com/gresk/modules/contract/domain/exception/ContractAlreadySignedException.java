package com.gresk.modules.contract.domain.exception;

public class ContractAlreadySignedException extends RuntimeException {
    public ContractAlreadySignedException() {
        super("Contract is already signed");
    }
}

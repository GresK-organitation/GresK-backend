package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.ScheduleDepositCommand;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.port.out.PaymentInstallmentRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleDepositUseCase {

    private final PaymentInstallmentRepositoryPort installmentRepository;

    public PaymentInstallment execute(ScheduleDepositCommand cmd) {
        Money amount = new Money(cmd.amount(), cmd.currency() != null ? cmd.currency() : "EUR");
        PaymentInstallment installment = PaymentInstallment.scheduleDeposit(
                PromoterId.of(cmd.promoterId()), UUID.fromString(cmd.linkedContractId()),
                amount, cmd.dueDate(), cmd.description());
        return installmentRepository.save(installment);
    }
}

package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.RecordInstallmentPaymentCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.PaymentInstallmentNotFoundException;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.model.PaymentInstallmentId;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingApplication;
import com.gresk.modules.finance.domain.model.valueobject.WithholdingKind;
import com.gresk.modules.finance.domain.port.out.PaymentInstallmentRepositoryPort;
import com.gresk.modules.finance.domain.service.WithholdingCalculator;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordInstallmentPaymentUseCase {

    private final PaymentInstallmentRepositoryPort installmentRepository;

    public PaymentInstallment execute(RecordInstallmentPaymentCommand cmd) {
        PaymentInstallment installment = installmentRepository.findById(PaymentInstallmentId.of(cmd.installmentId()))
                .orElseThrow(() -> new PaymentInstallmentNotFoundException(cmd.installmentId()));

        if (!installment.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        WithholdingKind kind = cmd.withholdingKind() != null ? cmd.withholdingKind() : WithholdingKind.NONE;
        WithholdingApplication withholding = WithholdingCalculator.apply(
                installment.getAmount(), kind, cmd.withholdingRatePercentage(), cmd.exemptionReason());

        installment.markPaid(cmd.paidDate(), cmd.paymentMethod(), withholding);
        return installmentRepository.save(installment);
    }
}

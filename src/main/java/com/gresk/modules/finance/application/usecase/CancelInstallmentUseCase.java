package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.command.CancelInstallmentCommand;
import com.gresk.modules.finance.domain.exception.FinanceResourceNotOwnedException;
import com.gresk.modules.finance.domain.exception.PaymentInstallmentNotFoundException;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.model.PaymentInstallmentId;
import com.gresk.modules.finance.domain.port.out.PaymentInstallmentRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelInstallmentUseCase {

    private final PaymentInstallmentRepositoryPort installmentRepository;

    public void execute(CancelInstallmentCommand cmd) {
        PaymentInstallment installment = installmentRepository.findById(PaymentInstallmentId.of(cmd.installmentId()))
                .orElseThrow(() -> new PaymentInstallmentNotFoundException(cmd.installmentId()));

        if (!installment.getPromoterId().equals(PromoterId.of(cmd.promoterId()))) {
            throw new FinanceResourceNotOwnedException();
        }

        installment.cancel();
        installmentRepository.save(installment);
    }
}

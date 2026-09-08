package com.gresk.modules.finance.application.usecase;

import com.gresk.modules.finance.application.query.GetPaymentScheduleQuery;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.port.out.PaymentInstallmentRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPaymentScheduleUseCase {

    private final PaymentInstallmentRepositoryPort installmentRepository;

    public List<PaymentInstallment> execute(GetPaymentScheduleQuery query) {
        PromoterId promoterId = PromoterId.of(query.promoterId());
        return installmentRepository.findByLinkedContractId(UUID.fromString(query.linkedContractId())).stream()
                .filter(i -> i.getPromoterId().equals(promoterId))
                .toList();
    }
}

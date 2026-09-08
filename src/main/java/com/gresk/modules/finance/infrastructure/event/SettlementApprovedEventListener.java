package com.gresk.modules.finance.infrastructure.event;

import com.gresk.modules.finance.application.event.SettlementApprovedEvent;
import com.gresk.modules.finance.domain.model.PaymentInstallment;
import com.gresk.modules.finance.domain.port.out.PaymentInstallmentRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** Al aprobar una liquidación, genera automáticamente el installment del saldo a pagar al artista. */
@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementApprovedEventListener implements ApplicationListener<SettlementApprovedEvent> {

    private final PaymentInstallmentRepositoryPort installmentRepository;

    @Override
    @Async
    public void onApplicationEvent(SettlementApprovedEvent event) {
        try {
            PaymentInstallment balance = PaymentInstallment.scheduleBalance(
                    event.getPromoterId(), event.getSettlementId().value(), event.getArtistPayableAmount(),
                    LocalDate.now(), "Saldo de liquidación " + event.getSettlementId());
            installmentRepository.save(balance);
        } catch (Exception e) {
            log.warn("Failed to schedule balance installment for settlement {}: {}",
                    event.getSettlementId(), e.getMessage());
        }
    }
}

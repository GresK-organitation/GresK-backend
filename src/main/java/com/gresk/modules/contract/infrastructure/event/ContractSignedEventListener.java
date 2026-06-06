package com.gresk.modules.contract.infrastructure.event;

import com.gresk.modules.agenda.application.command.CreateAgendaEntryCommand;
import com.gresk.modules.agenda.application.usecase.CreateAgendaEntryUseCase;
import com.gresk.modules.agenda.domain.model.EntryType;
import com.gresk.modules.contract.domain.model.ContractType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractSignedEventListener implements ApplicationListener<ContractSignedEvent> {

    private final CreateAgendaEntryUseCase createAgendaEntryUseCase;

    @Override
    @Async
    public void onApplicationEvent(ContractSignedEvent event) {
        if (event.getContractType() != ContractType.PERFORMANCE) return;
        if (event.getLinkedEventId() == null) return;

        try {
            String description = "Contrato de actuación firmado con " + event.getPartyBName()
                    + (event.getFeeAmount() != null ? ". Caché: " + event.getFeeAmount() + " EUR" : "");

            createAgendaEntryUseCase.execute(new CreateAgendaEntryCommand(
                    event.getPromoterId().toString(),
                    EntryType.TASK,
                    "Contrato firmado: " + event.getReferenceNumber(),
                    description,
                    Instant.now(),
                    null,
                    false,
                    "#2E7D32",
                    "Contrato",
                    null,
                    null,
                    null
            ));
        } catch (Exception e) {
            log.warn("Failed to create agenda entry for signed contract {}: {}",
                    event.getReferenceNumber(), e.getMessage());
        }
    }
}

package com.gresk.modules.agenda.infrastructure.scheduler;

import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.port.out.AgendaEntryRepository;
import com.gresk.modules.agenda.domain.port.out.MailNotificationPort;
import com.gresk.modules.promoter.infrastructure.persitence.PromoterJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderEmailScheduler {

    private final AgendaEntryRepository agendaRepository;
    private final MailNotificationPort  mailPort;
    private final PromoterJpaRepository promoterRepo;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void sendPendingReminders() {
        List<AgendaEntry> pending = agendaRepository.findPendingReminders(Instant.now());

        if (pending.isEmpty()) return;
        log.debug("Processing {} pending reminder(s)", pending.size());

        for (AgendaEntry entry : pending) {
            promoterRepo.findById(entry.getPromoterId().value()).ifPresent(promoter -> {
                mailPort.sendReminderEmail(entry, promoter.getEmail());
                entry.markReminderSent();
                agendaRepository.save(entry);
            });
        }
    }
}

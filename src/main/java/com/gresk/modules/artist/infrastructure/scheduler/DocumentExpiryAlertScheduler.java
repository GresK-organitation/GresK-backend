package com.gresk.modules.artist.infrastructure.scheduler;

import com.gresk.modules.artist.application.port.in.CheckDocumentExpiryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentExpiryAlertScheduler {

    private final CheckDocumentExpiryPort checkDocumentExpiry;

    @Scheduled(cron = "0 0 6 * * *")
    public void checkExpiringDocuments() {
        log.info("Document expiry alert job triggered");
        checkDocumentExpiry.execute();
    }
}

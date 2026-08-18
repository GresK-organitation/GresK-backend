package com.gresk.modules.tendencias.chronicle.infrastructure.scheduler;

import com.gresk.modules.tendencias.chronicle.application.usecase.IngestChroniclesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IngestChroniclesJob {

    private final IngestChroniclesUseCase ingestChroniclesUseCase;

    @Scheduled(fixedDelayString = "${gresk.tendencias.chronicle.polling-interval-ms:1800000}")
    public void run() {
        ingestChroniclesUseCase.execute();
    }
}

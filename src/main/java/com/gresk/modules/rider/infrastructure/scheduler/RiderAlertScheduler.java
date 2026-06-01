package com.gresk.modules.rider.infrastructure.scheduler;

import com.gresk.modules.rider.application.usecase.SendRiderAlertUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiderAlertScheduler {

    private final SendRiderAlertUseCase sendRiderAlertUseCase;

    @Scheduled(fixedRate = 3_600_000)
    public void checkRiderAlerts() {
        log.info("Rider alert scheduler triggered at {}", Instant.now());
        Instant now       = Instant.now();
        Instant windowEnd = now.plus(72, ChronoUnit.HOURS);
        sendRiderAlertUseCase.execute(now, windowEnd);
    }
}

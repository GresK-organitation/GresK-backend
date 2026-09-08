package com.gresk.modules.show.infrastructure.scheduler;

import com.gresk.modules.show.application.port.in.ExpireHoldsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShowHoldExpirationScheduler {

    private final ExpireHoldsUseCase expireHoldsUseCase;

    @Scheduled(fixedDelay = 60_000)
    public void expireOverdueHolds() {
        int expired = expireHoldsUseCase.execute();
        if (expired > 0) {
            log.info("Expired {} overdue show hold(s)", expired);
        }
    }
}

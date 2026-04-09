package com.gresk.modules.promoter.infrastructure.event;

import com.gresk.modules.promoter.application.command.RegisterPromoterCommand;
import com.gresk.modules.promoter.application.port.in.RegisterPromoterPort;
import com.gresk.shared.domain.event.PromoterRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromoterModuleEventListener {

    private final RegisterPromoterPort registerPromoterUseCase;

    @EventListener
    public void on(PromoterRegisteredEvent event) {
        registerPromoterUseCase.execute(new RegisterPromoterCommand(
                event.promoterId(),
                event.email(),
                event.companyName(),
                event.street(),
                event.city(),
                event.country(),
                event.description(),
                event.musicalGenres(),
                event.logoAssetId(),
                event.phone(),
                event.website()
        ));
    }
}

package com.gresk.modules.ticket.infrastructure;

import com.gresk.modules.ticket.application.usecase.GetTicketQrUseCase;
import com.gresk.modules.ticket.application.usecase.GetUserTicketsUseCase;
import com.gresk.modules.ticket.domain.port.out.QrCodeGenerator;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TicketModuleConfiguration {

    @Bean
    public GetUserTicketsUseCase getUserTicketsUseCase(TicketRepository ticketRepository) {
        return new GetUserTicketsUseCase(ticketRepository);
    }

    @Bean
    public GetTicketQrUseCase getTicketQrUseCase(TicketRepository ticketRepository,
                                                  QrCodeGenerator qrCodeGenerator) {
        return new GetTicketQrUseCase(ticketRepository, qrCodeGenerator);
    }
}

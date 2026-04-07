package com.gresk.modules.ticket.application.usecase;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.ticket.domain.model.QrCode;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserTicketsUseCaseTest {

    @Mock private TicketRepository ticketRepository;

    private GetUserTicketsUseCase useCase;
    private UserId userId;

    @BeforeEach
    void setUp() {
        useCase = new GetUserTicketsUseCase(ticketRepository);
        userId = UserId.generate();
    }

    @Test
    void execute_returnsListOfTicketsForUser() {
        Ticket ticket = Ticket.purchase(userId, EventId.generate(), QrCode.of("qr-token"));
        when(ticketRepository.findByUserId(any())).thenReturn(List.of(ticket));

        List<Ticket> result = useCase.execute(new GetUserTicketsQuery(userId.value().toString()));

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(ticket);
    }

    @Test
    void execute_returnsEmptyListWhenUserHasNoTickets() {
        when(ticketRepository.findByUserId(any())).thenReturn(List.of());

        List<Ticket> result = useCase.execute(new GetUserTicketsQuery(userId.value().toString()));

        assertThat(result).isEmpty();
    }
}

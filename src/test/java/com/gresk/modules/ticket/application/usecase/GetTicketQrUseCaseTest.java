package com.gresk.modules.ticket.application.usecase;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.ticket.domain.exception.TicketNotFoundException;
import com.gresk.modules.ticket.domain.exception.UnauthorizedTicketAccessException;
import com.gresk.modules.ticket.domain.model.QrCode;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.domain.port.out.QrCodeGenerator;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTicketQrUseCaseTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private QrCodeGenerator qrCodeGenerator;

    private GetTicketQrUseCase useCase;
    private UserId userId;

    @BeforeEach
    void setUp() {
        useCase = new GetTicketQrUseCase(ticketRepository, qrCodeGenerator);
        userId = UserId.generate();
    }

    @Test
    void execute_returnsPngBytesForValidOwner() {
        Ticket ticket = Ticket.purchase(userId, EventId.generate(), QrCode.of("qr-token"));
        byte[] pngBytes = new byte[]{(byte) 0x89, 'P', 'N', 'G'};

        when(ticketRepository.findById(any())).thenReturn(Optional.of(ticket));
        when(qrCodeGenerator.renderToImage(any())).thenReturn(pngBytes);

        byte[] result = useCase.execute(new GetTicketQrQuery(
                ticket.getId().value().toString(), userId.value().toString()));

        assertThat(result).isEqualTo(pngBytes);
    }

    @Test
    void execute_throwsTicketNotFoundExceptionForAbsentTicket() {
        when(ticketRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new GetTicketQrQuery(
                java.util.UUID.randomUUID().toString(), userId.value().toString())))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void execute_throwsUnauthorizedTicketAccessExceptionForWrongUser() {
        UserId ownerUserId = UserId.generate();
        UserId otherUserId = UserId.generate();
        Ticket ticket = Ticket.purchase(ownerUserId, EventId.generate(), QrCode.of("qr-token"));

        when(ticketRepository.findById(any())).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> useCase.execute(new GetTicketQrQuery(
                ticket.getId().value().toString(), otherUserId.value().toString())))
                .isInstanceOf(UnauthorizedTicketAccessException.class);
    }
}

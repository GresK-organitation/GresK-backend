package com.gresk.modules.ticket.application.usecase;

import com.gresk.modules.event.domain.exception.EventNotFoundException;
import com.gresk.modules.event.domain.exception.EventSoldOutException;
import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.ticket.domain.exception.DuplicateTicketException;
import com.gresk.modules.ticket.domain.exception.PaymentFailedException;
import com.gresk.modules.ticket.domain.model.*;
import com.gresk.modules.ticket.domain.port.out.PaymentGateway;
import com.gresk.modules.ticket.domain.port.out.QrCodeGenerator;
import com.gresk.modules.ticket.domain.port.out.TicketRepository;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseTicketUseCaseTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private EventRepository eventRepository;
    @Mock private PaymentGateway paymentGateway;
    @Mock private QrCodeGenerator qrCodeGenerator;

    private PurchaseTicketUseCase useCase;

    private UserId userId;
    private EventId eventId;
    private Event publishedEvent;

    @BeforeEach
    void setUp() {
        useCase = new PurchaseTicketUseCase(ticketRepository, eventRepository, paymentGateway, qrCodeGenerator);
        userId = UserId.generate();
        eventId = EventId.generate();
        publishedEvent = Event.create("Test Event", PromoterId.generate())
                .withGenre(Genre.ELECTRONIC)
                .withPrice(new Price(new BigDecimal("20.00"), "EUR"))
                .withCapacity(Capacity.of(100))
                .withEventDate(LocalDateTime.now().plusMonths(1))
                .withLocation(new Location("Madrid", "Gran Via 1", "Sala"));
        publishedEvent.publish();
    }

    @Test
    void execute_successfulPurchase_returnsPurchasedTicket() {
        when(ticketRepository.existsByUserIdAndEventId(any(), any())).thenReturn(false);
        when(eventRepository.findByIdWithLock(any())).thenReturn(Optional.of(publishedEvent));
        when(paymentGateway.processPayment(any(), any(), any()))
                .thenReturn(new PaymentResult(true, "txn-123"));
        when(qrCodeGenerator.generate(any())).thenReturn(QrCode.of("qr-token"));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = useCase.execute(new PurchaseTicketCommand(
                userId.value().toString(), eventId.value().toString()));

        assertThat(result.getStatus()).isEqualTo(TicketStatus.PURCHASED);
        assertThat(result.getQrCode()).isEqualTo(QrCode.of("qr-token"));
    }

    @Test
    void execute_nonExistentEvent_throwsEventNotFoundException() {
        when(ticketRepository.existsByUserIdAndEventId(any(), any())).thenReturn(false);
        when(eventRepository.findByIdWithLock(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new PurchaseTicketCommand(
                userId.value().toString(), eventId.value().toString())))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    void execute_soldOutEvent_throwsEventSoldOutException() {
        Event soldOutEvent = Event.create("Sold Out", PromoterId.generate())
                .withGenre(Genre.ELECTRONIC)
                .withPrice(new Price(new BigDecimal("20.00"), "EUR"))
                .withCapacity(Capacity.of(1))
                .withEventDate(LocalDateTime.now().plusMonths(1))
                .withLocation(new Location("Madrid", "Gran Via 1", "Sala"));
        soldOutEvent.publish();
        soldOutEvent.decrementCapacity();

        when(ticketRepository.existsByUserIdAndEventId(any(), any())).thenReturn(false);
        when(eventRepository.findByIdWithLock(any())).thenReturn(Optional.of(soldOutEvent));

        assertThatThrownBy(() -> useCase.execute(new PurchaseTicketCommand(
                userId.value().toString(), eventId.value().toString())))
                .isInstanceOf(EventSoldOutException.class);
    }

    @Test
    void execute_failedPayment_throwsPaymentFailedException() {
        when(ticketRepository.existsByUserIdAndEventId(any(), any())).thenReturn(false);
        when(eventRepository.findByIdWithLock(any())).thenReturn(Optional.of(publishedEvent));
        when(paymentGateway.processPayment(any(), any(), any()))
                .thenReturn(new PaymentResult(false, "txn-fail"));

        assertThatThrownBy(() -> useCase.execute(new PurchaseTicketCommand(
                userId.value().toString(), eventId.value().toString())))
                .isInstanceOf(PaymentFailedException.class);
    }

    @Test
    void execute_duplicateTicket_throwsDuplicateTicketException() {
        when(ticketRepository.existsByUserIdAndEventId(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(new PurchaseTicketCommand(
                userId.value().toString(), eventId.value().toString())))
                .isInstanceOf(DuplicateTicketException.class);
    }

    @Test
    void execute_success_decrementsCapacityExactlyOnce() {
        when(ticketRepository.existsByUserIdAndEventId(any(), any())).thenReturn(false);
        when(eventRepository.findByIdWithLock(any())).thenReturn(Optional.of(publishedEvent));
        when(paymentGateway.processPayment(any(), any(), any()))
                .thenReturn(new PaymentResult(true, "txn-123"));
        when(qrCodeGenerator.generate(any())).thenReturn(QrCode.of("qr-token"));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new PurchaseTicketCommand(
                userId.value().toString(), eventId.value().toString()));

        assertThat(publishedEvent.getCapacity().available()).isEqualTo(99);
    }

    @Test
    void execute_success_qrCodeIsGeneratedAndSetOnTicket() {
        QrCode expectedQrCode = QrCode.of("unique-qr-token");
        when(ticketRepository.existsByUserIdAndEventId(any(), any())).thenReturn(false);
        when(eventRepository.findByIdWithLock(any())).thenReturn(Optional.of(publishedEvent));
        when(paymentGateway.processPayment(any(), any(), any()))
                .thenReturn(new PaymentResult(true, "txn-123"));
        when(qrCodeGenerator.generate(any())).thenReturn(expectedQrCode);
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = useCase.execute(new PurchaseTicketCommand(
                userId.value().toString(), eventId.value().toString()));

        assertThat(result.getQrCode()).isEqualTo(expectedQrCode);
        verify(qrCodeGenerator).generate(any(TicketId.class));
    }

    @Test
    void execute_success_eventIsSavedAfterDecrement() {
        when(ticketRepository.existsByUserIdAndEventId(any(), any())).thenReturn(false);
        when(eventRepository.findByIdWithLock(any())).thenReturn(Optional.of(publishedEvent));
        when(paymentGateway.processPayment(any(), any(), any()))
                .thenReturn(new PaymentResult(true, "txn-123"));
        when(qrCodeGenerator.generate(any())).thenReturn(QrCode.of("qr-token"));
        when(ticketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(new PurchaseTicketCommand(
                userId.value().toString(), eventId.value().toString()));

        verify(eventRepository).save(publishedEvent);
    }
}

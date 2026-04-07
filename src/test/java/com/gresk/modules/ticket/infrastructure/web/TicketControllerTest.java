package com.gresk.modules.ticket.infrastructure.web;

import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.ticket.application.usecase.GetTicketQrUseCase;
import com.gresk.modules.ticket.application.usecase.GetUserTicketsUseCase;
import com.gresk.modules.ticket.domain.model.QrCode;
import com.gresk.modules.ticket.domain.model.Ticket;
import com.gresk.modules.ticket.infrastructure.TransactionalPurchaseTicketService;
import com.gresk.modules.ticket.infrastructure.web.dto.TicketResponse;
import com.gresk.modules.promoter.infrastructure.web.GlobalExceptionHandler;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock private TransactionalPurchaseTicketService purchaseTicketService;
    @Mock private GetUserTicketsUseCase getUserTicketsUseCase;
    @Mock private GetTicketQrUseCase getTicketQrUseCase;

    private MockMvc mockMvc;
    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = UserId.generate();
        TicketController controller = new TicketController(
                purchaseTicketService, getUserTicketsUseCase, getTicketQrUseCase);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(userId, null, "ROLE_USER"));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void purchase_withValidBody_returns201() throws Exception {
        Ticket ticket = Ticket.purchase(userId, EventId.generate(), QrCode.of("qr-token"));
        when(purchaseTicketService.execute(any())).thenReturn(ticket);

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventId": "550e8400-e29b-41d4-a716-446655440000"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("PURCHASED"));
    }

    @Test
    void purchase_withoutEventId_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"eventId": ""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listMyTickets_returns200WithList() throws Exception {
        Ticket ticket = Ticket.purchase(userId, EventId.generate(), QrCode.of("qr-token"));
        when(getUserTicketsUseCase.execute(any())).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/v1/users/me/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("PURCHASED"));
    }

    @Test
    void getQr_returns200WithPngContentType() throws Exception {
        byte[] pngBytes = new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};
        when(getTicketQrUseCase.execute(any())).thenReturn(pngBytes);

        mockMvc.perform(get("/api/v1/tickets/{id}/qr",
                        java.util.UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }
}

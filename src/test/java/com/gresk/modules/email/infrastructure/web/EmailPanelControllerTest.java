package com.gresk.modules.email.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.application.usecase.*;
import com.gresk.modules.email.domain.exception.EmailMessageNotFoundException;
import com.gresk.modules.email.domain.exception.ForbiddenEmailOperationException;
import com.gresk.modules.email.domain.model.EmailMessage;
import com.gresk.modules.email.domain.model.EmailMessageId;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmailPanelControllerTest {

    private static final UUID PROMOTER_ID = UUID.randomUUID();

    @Mock private GetEventEmailTimelineUseCase timeline;
    @Mock private GetEventEmailSummaryUseCase  summary;
    @Mock private GetPromoterEmailsUseCase     promoterEmails;
    @Mock private GetEmailDetailUseCase        emailDetail;
    @Mock private LinkEmailToEventUseCase      linkToEvent;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new EmailPanelController(
                        timeline, summary, promoterEmails, emailDetail, linkToEvent,
                        new EmailResponseMapper(new ObjectMapper())))
                .setControllerAdvice(new EmailExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(PROMOTER_ID.toString(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_PROMOTER"))));
    }

    @Test
    void laBandejaDelPromotorDevuelveSusEmails() throws Exception {
        when(promoterEmails.execute(PROMOTER_ID)).thenReturn(List.of(email("Rider actualizado")));

        mvc.perform(get("/api/v1/promoters/me/emails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subject").value("Rider actualizado"))
                .andExpect(jsonPath("$[0].fromAddress").value("promotor@sala.com"))
                .andExpect(jsonPath("$[0].processingStatus").value("PENDING"));
    }

    @Test
    void unEmailInexistenteDevuelve404() throws Exception {
        UUID emailId = UUID.randomUUID();
        when(emailDetail.execute(any(), any()))
                .thenThrow(new EmailMessageNotFoundException(EmailMessageId.of(emailId)));

        mvc.perform(get("/api/v1/promoters/me/emails/" + emailId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void unEmailDeOtroPromotorDevuelve403() throws Exception {
        when(emailDetail.execute(any(), any()))
                .thenThrow(new ForbiddenEmailOperationException("not yours"));

        mvc.perform(get("/api/v1/promoters/me/emails/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void elTimelineAceptaPaginacion() throws Exception {
        when(timeline.execute(any(), any(), org.mockito.ArgumentMatchers.eq(1),
                org.mockito.ArgumentMatchers.eq(5)))
                .thenReturn(List.of());

        mvc.perform(get("/api/v1/promoters/me/events/" + UUID.randomUUID() + "/emails")
                        .param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    private EmailMessage email(String subject) {
        return EmailMessage.receive(
                PromoterId.of(PROMOTER_ID), "msg-1", "thread-1",
                "promotor@sala.com", "Sala Apolo", List.of(),
                subject, "cuerpo", null, null, Instant.now());
    }
}

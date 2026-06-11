package com.gresk.modules.email.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.application.usecase.*;
import com.gresk.modules.email.domain.exception.InvalidDraftReplyStatusException;
import com.gresk.modules.email.domain.model.EmailDraftReply;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DraftReplyControllerTest {

    private static final UUID PROMOTER_ID = UUID.randomUUID();

    @Mock private GetPendingDraftsUseCase  pendingDrafts;
    @Mock private GetDraftDetailUseCase    draftDetail;
    @Mock private EditDraftUseCase         editDraft;
    @Mock private ApproveDraftReplyUseCase approveDraft;
    @Mock private DiscardDraftUseCase      discardDraft;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new DraftReplyController(
                        pendingDrafts, draftDetail, editDraft, approveDraft, discardDraft,
                        new EmailResponseMapper(new ObjectMapper())))
                .setControllerAdvice(new EmailExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(PROMOTER_ID.toString(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_PROMOTER"))));
    }

    @Test
    void aprobarUnBorradorYaEnviadoDevuelve409() throws Exception {
        when(approveDraft.execute(any()))
                .thenThrow(new InvalidDraftReplyStatusException("Cannot approve a draft in status: SENT"));

        mvc.perform(post("/api/v1/promoters/me/drafts/" + UUID.randomUUID() + "/approve")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Cannot approve a draft in status: SENT"));
    }

    @Test
    void losBorradoresPendientesSeDevuelvenConSuEstado() throws Exception {
        when(pendingDrafts.execute(PROMOTER_ID)).thenReturn(List.of(draft()));

        mvc.perform(get("/api/v1/promoters/me/drafts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING_REVIEW"))
                .andExpect(jsonPath("$[0].draftType").value("RIDER"));
    }

    @Test
    void descartarUnBorradorDevuelve204() throws Exception {
        mvc.perform(delete("/api/v1/promoters/me/drafts/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());

        verify(discardDraft).execute(any(), any());
    }

    @Test
    void editarConCuerpoVacioDevuelve400() throws Exception {
        mvc.perform(put("/api/v1/promoters/me/drafts/" + UUID.randomUUID())
                        .contentType(APPLICATION_JSON)
                        .content("{\"body\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    private EmailDraftReply draft() {
        return EmailDraftReply.create(
                EmailMessageId.generate(), PromoterId.of(PROMOTER_ID),
                "RIDER", "Re: Rider", "Cuerpo generado por IA");
    }
}

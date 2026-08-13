package com.gresk.modules.journal.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.journal.application.dto.BulkCreateFailure;
import com.gresk.modules.journal.application.dto.BulkCreateResult;
import com.gresk.modules.journal.application.port.in.CreateJournalEntryPort;
import com.gresk.modules.journal.application.port.in.DeleteJournalEntryPort;
import com.gresk.modules.journal.application.port.in.UpdateJournalEntryPort;
import com.gresk.modules.journal.application.usecase.AddJournalMediaUseCase;
import com.gresk.modules.journal.application.usecase.BulkCreateJournalEntriesUseCase;
import com.gresk.modules.journal.application.usecase.GetJournalEntryUseCase;
import com.gresk.modules.journal.application.usecase.GetSuggestedRatingCriteriaUseCase;
import com.gresk.modules.journal.application.usecase.ListMyJournalEntriesUseCase;
import com.gresk.modules.journal.application.usecase.RemoveJournalMediaUseCase;
import com.gresk.modules.journal.domain.exception.JournalEntryForbiddenException;
import com.gresk.modules.journal.domain.exception.JournalEntryNotFoundException;
import com.gresk.modules.journal.domain.model.ApproxDate;
import com.gresk.modules.journal.domain.model.JournalEntry;
import com.gresk.modules.journal.domain.model.JournalEntrySource;
import com.gresk.modules.journal.domain.model.JournalVisibility;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.port.out.ImageUrlResolverPort;
import com.gresk.shared.domain.port.out.VideoUrlResolverPort;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class JournalEntryControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock private CreateJournalEntryPort            createJournalEntryPort;
    @Mock private UpdateJournalEntryPort            updateJournalEntryPort;
    @Mock private DeleteJournalEntryPort            deleteJournalEntryPort;
    @Mock private GetJournalEntryUseCase            getJournalEntryUseCase;
    @Mock private ListMyJournalEntriesUseCase       listMyJournalEntriesUseCase;
    @Mock private BulkCreateJournalEntriesUseCase   bulkCreateJournalEntriesUseCase;
    @Mock private AddJournalMediaUseCase            addJournalMediaUseCase;
    @Mock private RemoveJournalMediaUseCase         removeJournalMediaUseCase;
    @Mock private GetSuggestedRatingCriteriaUseCase getSuggestedRatingCriteriaUseCase;
    @Mock private ImageUrlResolverPort              imageUrlResolverPort;
    @Mock private VideoUrlResolverPort              videoUrlResolverPort;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        JournalEntryResponseMapper mapper = new JournalEntryResponseMapper(imageUrlResolverPort, videoUrlResolverPort);

        mvc = MockMvcBuilders.standaloneSetup(new JournalEntryController(
                        createJournalEntryPort, updateJournalEntryPort, deleteJournalEntryPort,
                        getJournalEntryUseCase, listMyJournalEntriesUseCase, bulkCreateJournalEntriesUseCase,
                        addJournalMediaUseCase, removeJournalMediaUseCase, getSuggestedRatingCriteriaUseCase,
                        mapper))
                .setControllerAdvice(new JournalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_ID.toString(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    private JournalEntry entry() {
        return JournalEntry.create(UserId.of(USER_ID), "Radiohead", null, ApproxDate.ofYear(2019),
                "Sala Apolo", "Barcelona", null, "Gran concierto", List.of(),
                null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);
    }

    @Test
    void creaUnaEntradaYDevuelve201() throws Exception {
        when(createJournalEntryPort.execute(any())).thenReturn(entry());

        String body = """
                {
                  "artistName": "Radiohead",
                  "date": "2019-01-01",
                  "datePrecision": "YEAR",
                  "venueName": "Sala Apolo",
                  "city": "Barcelona",
                  "notes": "Gran concierto",
                  "criteria": [],
                  "visibility": "PRIVATE"
                }
                """;

        mvc.perform(post("/api/v1/journal/entries").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.artistName").value("Radiohead"))
                .andExpect(jsonPath("$.venueName").value("Sala Apolo"));
    }

    @Test
    void unaEntradaInexistenteDevuelve404() throws Exception {
        UUID entryId = UUID.randomUUID();
        when(getJournalEntryUseCase.execute(any(), any()))
                .thenThrow(new JournalEntryNotFoundException("not found"));

        mvc.perform(get("/api/v1/journal/entries/" + entryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void unaEntradaDeOtroUsuarioDevuelve403() throws Exception {
        when(getJournalEntryUseCase.execute(any(), any()))
                .thenThrow(new JournalEntryForbiddenException("forbidden"));

        mvc.perform(get("/api/v1/journal/entries/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarMisEntradasDevuelvePaginado() throws Exception {
        when(listMyJournalEntriesUseCase.execute(any())).thenReturn(List.of(entry()));
        when(listMyJournalEntriesUseCase.count(any())).thenReturn(1L);

        mvc.perform(get("/api/v1/journal/entries/mine").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].artistName").value("Radiohead"));
    }

    @Test
    void borrarUnaEntradaDevuelve204() throws Exception {
        mvc.perform(delete("/api/v1/journal/entries/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void altaEnBloqueConExitosYFallosParciales() throws Exception {
        when(bulkCreateJournalEntriesUseCase.execute(any()))
                .thenReturn(new BulkCreateResult(List.of(entry()), List.of(new BulkCreateFailure(1, "Artist not found"))));

        String body = """
                {
                  "entries": [
                    {"artistName": "Radiohead", "date": "2019-01-01", "datePrecision": "YEAR", "criteria": [], "visibility": "PRIVATE"},
                    {"artistId": "%s", "date": "2018-01-01", "datePrecision": "YEAR", "criteria": [], "visibility": "PRIVATE"}
                  ]
                }
                """.formatted(UUID.randomUUID());

        mvc.perform(post("/api/v1/journal/entries/bulk").contentType(APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.created.length()").value(1))
                .andExpect(jsonPath("$.failures.length()").value(1))
                .andExpect(jsonPath("$.failures[0].reason").value("Artist not found"));
    }

    @Test
    void plantillasSugeridasDevuelveLista() throws Exception {
        when(getSuggestedRatingCriteriaUseCase.execute(any()))
                .thenReturn(List.of("Improvisación", "Sonido"));

        mvc.perform(get("/api/v1/journal/rating-templates").param("genre", "JAZZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestions[0]").value("Improvisación"));
    }
}

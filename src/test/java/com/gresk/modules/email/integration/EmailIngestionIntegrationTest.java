package com.gresk.modules.email.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.application.command.IngestEmailCommand;
import com.gresk.modules.email.application.dto.EventEmailSummary;
import com.gresk.modules.email.application.usecase.GetEventEmailSummaryUseCase;
import com.gresk.modules.email.application.usecase.IngestEmailUseCase;
import com.gresk.modules.email.domain.model.*;
import com.gresk.modules.email.domain.model.EmailProcessingResult.ExtractedEntity;
import com.gresk.modules.email.domain.port.out.*;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * E2E del Email Intelligence Engine sobre PostgreSQL real (Testcontainers):
 * ingesta → clasificación asíncrona (IA mockeada, 0 tokens) → entidades,
 * rider versionado con diff, borradores con envío SMTP real (GreenMail)
 * y seguridad de los endpoints del panel.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class EmailIngestionIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withPerMethodLifecycle(false);

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private static final UUID PROMOTER_A = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
    private static final UUID PROMOTER_B = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000002");
    private static final UUID EVENT_ID   = UUID.fromString("eeeeeeee-0000-0000-0000-000000000001");

    @MockitoBean private LocalEmailClassifierPort localClassifier;
    @MockitoBean private AiEmailProcessorPort     aiProcessor;

    @Autowired private IngestEmailUseCase             ingest;
    @Autowired private GetEventEmailSummaryUseCase    summaryUseCase;
    @Autowired private EmailMessageRepositoryPort     emailRepo;
    @Autowired private EmailEntityRecordRepositoryPort entityRepo;
    @Autowired private EmailRiderVersionRepositoryPort riderRepo;
    @Autowired private EmailDraftReplyRepositoryPort   draftRepo;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private MockMvc      mvc;
    @Autowired private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM email_entities");
        jdbc.update("DELETE FROM email_draft_replies");
        jdbc.update("DELETE FROM email_rider_versions");
        jdbc.update("DELETE FROM email_messages");
        seedPromoter(PROMOTER_A, "promotora-a@gresk.com");
        seedPromoter(PROMOTER_B, "promotora-b@gresk.com");
        jdbc.update("""
                INSERT INTO events (id, title, promoter_id) VALUES (?, 'Festival GresK', ?)
                ON CONFLICT (id) DO NOTHING
                """, EVENT_ID, PROMOTER_A);

        when(localClassifier.classify(any(), any())).thenReturn(ClassificationResult.lowConfidence());
    }

    // ── Flujo completo: ingesta → clasificación → rider versionado ────────────

    @Test
    @DisplayName("Flujo completo: email rider → procesado → rider v1 creado")
    void emailDeRiderCreaLaPrimeraVersionDelRider() {
        stubClaudeRider(Map.of("pa_system", "Line Array 10kW"),
                new ExtractedEntity(ExtractedEntityType.RIDER_ITEM, "pa_system",
                        "Line Array 10kW", null, 0.97, "necesitamos Line Array", false));

        seedThreadLinkedToEvent("thread-rider");
        ingest.execute(command("msg-test-001", "thread-rider", "Rider técnico",
                "Necesitamos Line Array 10kW y 4 monitores wedge."));

        awaitProcessed("msg-test-001");

        EmailMessage processed = emailRepo.findByExternalMessageId("msg-test-001").orElseThrow();
        assertThat(processed.getClassification()).isEqualTo(EmailClassification.RIDER);
        assertThat(processed.getEventId()).isEqualTo(EVENT_ID);
        assertThat(entityRepo.findByEmailId(processed.getId())).hasSize(1);

        EmailRiderVersion rider = riderRepo.findLatestByEventId(EVENT_ID).orElseThrow();
        assertThat(rider.getVersionNumber()).isEqualTo(1);
        assertThat(rider.getRiderDataJson()).contains("pa_system");
        assertThat(rider.getDiffFromPrevJson()).isNull(); // primera versión, sin diff
        assertThat(rider.getCreatedBy()).isEqualTo(RiderVersionSource.AI);
    }

    @Test
    @DisplayName("Email duplicado no se procesa dos veces")
    void emailDuplicadoNoSeProcesaDosVeces() {
        stubClaudeOtro();

        ingest.execute(command("msg-dup-001", "thread-dup", "Hola", "Texto cualquiera"));
        ingest.execute(command("msg-dup-001", "thread-dup", "Hola", "Texto cualquiera"));

        awaitProcessed("msg-dup-001");

        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM email_messages WHERE message_id_external = 'msg-dup-001'",
                Integer.class);
        assertThat(count).isEqualTo(1);
        verify(aiProcessor, atMostOnce()).process(any(), any());
    }

    @Test
    @DisplayName("Rider v2 genera diff automático respecto a v1")
    void laSegundaVersionDelRiderIncluyeElDiff() throws Exception {
        seedThreadLinkedToEvent("thread-v");

        stubClaudeRider(Map.of("pa_system", "Line Array 10kW", "monitors", "4 wedge"));
        ingest.execute(command("msg-v1", "thread-v", "Rider", "v1"));
        awaitProcessed("msg-v1");

        stubClaudeRider(Map.of("pa_system", "Line Array 15kW", "di_boxes", "2"));
        ingest.execute(command("msg-v2", "thread-v", "Rider actualizado", "v2"));
        awaitProcessed("msg-v2");

        List<EmailRiderVersion> versions = riderRepo.findByEventId(EVENT_ID);
        assertThat(versions).hasSize(2);

        EmailRiderVersion v2 = versions.get(0);
        assertThat(v2.getVersionNumber()).isEqualTo(2);
        assertThat(v2.getDiffFromPrevJson()).isNotNull();

        RiderDiff diff = objectMapper.readValue(v2.getDiffFromPrevJson(), RiderDiff.class);
        assertThat(diff.added()).containsExactly("di_boxes: 2");
        assertThat(diff.removed()).containsExactly("monitors: 4 wedge");
        assertThat(diff.modified()).hasSize(1);
        assertThat(diff.modified().get(0).field()).isEqualTo("pa_system");
    }

    @Test
    @DisplayName("Pipeline híbrido E2E: reglas y Ollama fallan → Claude procesa")
    void elPipelineEscalaHastaClaudeCuandoLasCapasBaratasNoResuelven() {
        stubClaudeOtro();

        // asunto/cuerpo sin patrones de reglas y Ollama stubbeado a baja confianza
        ingest.execute(command("msg-amb-001", "thread-amb", "Hola", "Nos vemos pronto."));
        awaitProcessed("msg-amb-001");

        verify(localClassifier, times(1)).classify(any(), any());
        verify(aiProcessor, times(1)).process(any(), any());
    }

    // ── Borradores: aprobación → envío SMTP (GreenMail) ───────────────────────

    @Test
    @DisplayName("Flujo borrador: aprobar → enviar por SMTP → estado SENT; reaprobar → 409")
    void aprobarUnBorradorLoEnviaPorSmtp() throws Exception {
        EmailMessage original = emailRepo.save(EmailMessage.receive(
                PromoterId.of(PROMOTER_A), "msg-draft-001", "thread-d",
                "manager@artista.com", "Manager", List.of(), "Caché",
                "¿Confirmáis el caché?", null, null, Instant.now()));
        EmailDraftReply draft = draftRepo.save(EmailDraftReply.create(
                original.getId(), PromoterId.of(PROMOTER_A),
                "CACHE", "Re: Caché", "Confirmado: 1800 EUR por transferencia."));

        mvc.perform(post("/api/v1/promoters/me/drafts/" + draft.getId() + "/approve")
                        .with(authentication(promoterAuth(PROMOTER_A)))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(greenMail.getReceivedMessages()).hasSize(1);
        var message = greenMail.getReceivedMessages()[0];
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("manager@artista.com");
        assertThat(message.getSubject()).isEqualTo("Re: Caché");

        assertThat(draftRepo.findById(draft.getId()).orElseThrow().getStatus())
                .isEqualTo(DraftReplyStatus.SENT);

        // Reaprobar un borrador ya enviado → 409
        mvc.perform(post("/api/v1/promoters/me/drafts/" + draft.getId() + "/approve")
                        .with(authentication(promoterAuth(PROMOTER_A)))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    // ── Seguridad ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Sin rol PROMOTER, los endpoints del panel devuelven 403")
    void sinRolPromoterElPanelDevuelve403() throws Exception {
        Authentication user = new UsernamePasswordAuthenticationToken(
                PROMOTER_A.toString(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mvc.perform(get("/api/v1/promoters/me/emails").with(authentication(user)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/promoters/me/drafts").with(authentication(user)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/promoters/me/events/" + EVENT_ID + "/rider")
                        .with(authentication(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un promotor no puede acceder a los emails de otro promotor")
    void unPromotorNoVeLosEmailsDeOtro() throws Exception {
        EmailMessage emailOfA = emailRepo.save(EmailMessage.receive(
                PromoterId.of(PROMOTER_A), "msg-priv-001", "thread-p",
                "x@y.com", null, List.of(), "Privado", "secreto", null, null, Instant.now()));

        // El dueño sí puede verlo
        mvc.perform(get("/api/v1/promoters/me/emails/" + emailOfA.getId())
                        .with(authentication(promoterAuth(PROMOTER_A))))
                .andExpect(status().isOk());

        // Otro promotor recibe 403; y su timeline del evento ajeno, 404
        mvc.perform(get("/api/v1/promoters/me/emails/" + emailOfA.getId())
                        .with(authentication(promoterAuth(PROMOTER_B))))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/promoters/me/events/" + EVENT_ID + "/emails")
                        .with(authentication(promoterAuth(PROMOTER_B))))
                .andExpect(status().isNotFound());
    }

    // ── Rendimiento ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("El summary responde en < 200ms con 100 emails en la BBDD")
    void elSummaryRespondeRapidoCon100Emails() {
        for (int i = 0; i < 100; i++) {
            emailRepo.save(EmailMessage.reconstitute(
                    EmailMessageId.generate(), PromoterId.of(PROMOTER_A), EVENT_ID,
                    "msg-perf-" + i, "thread-perf", "p@s.com", null, List.of(),
                    "Email " + i, "cuerpo", null, null,
                    EmailClassification.RIDER, null,
                    ProcessingStatus.DONE, 1, null, Instant.now(),
                    Instant.now().minusSeconds(i), Instant.now()));
        }

        summaryUseCase.execute(EVENT_ID, PROMOTER_A); // calentamiento

        long start = System.nanoTime();
        EventEmailSummary summary = summaryUseCase.execute(EVENT_ID, PROMOTER_A);
        long elapsedMs = Duration.ofNanos(System.nanoTime() - start).toMillis();

        assertThat(summary.emailCount()).isEqualTo(100);
        assertThat(summary.classificationCounts()).containsEntry("RIDER", 100);
        assertThat(elapsedMs).isLessThan(200);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private void seedPromoter(UUID id, String email) {
        UUID accountId = UUID.nameUUIDFromBytes(("acc-" + id).getBytes());
        jdbc.update("""
                INSERT INTO accounts (id, email, password_hash, status)
                VALUES (?, ?, 'x', 'ACTIVE') ON CONFLICT (id) DO NOTHING
                """, accountId, "account-" + email);
        jdbc.update("""
                INSERT INTO users (id, account_id, email, name, city)
                VALUES (?, ?, ?, 'Promotora Test', 'Madrid') ON CONFLICT (id) DO NOTHING
                """, id, accountId, email);
        jdbc.update("""
                INSERT INTO user_roles (user_id, role) VALUES (?, 'PROMOTER')
                ON CONFLICT DO NOTHING
                """, id);
        // events.promoter_id referencia a promoters(id), no a users(id)
        jdbc.update("""
                INSERT INTO promoters (id, account_id, email, name, street, city, country)
                VALUES (?, ?, ?, 'Promotora Test', 'Gran Vía 1', 'Madrid', 'España')
                ON CONFLICT (id) DO NOTHING
                """, id, accountId, "promoter-" + email);
    }

    /** Un correo previo del hilo ya vinculado al evento: el EventLinker hará el resto. */
    private void seedThreadLinkedToEvent(String threadId) {
        emailRepo.save(EmailMessage.reconstitute(
                EmailMessageId.generate(), PromoterId.of(PROMOTER_A), EVENT_ID,
                "seed-" + threadId, threadId, "manager@artista.com", null, List.of(),
                "Hilo del evento", "primer correo", null, null,
                EmailClassification.CONFIRMACION, null,
                ProcessingStatus.DONE, 1, null, Instant.now(),
                Instant.now().minusSeconds(3600), Instant.now()));
    }

    private IngestEmailCommand command(String externalId, String threadId,
                                       String subject, String body) {
        return new IngestEmailCommand(PROMOTER_A, externalId, threadId,
                "manager@artista.com", "Management", List.of("promotora-a@gresk.com"),
                subject, body, null, null, Instant.now());
    }

    private void stubClaudeRider(Map<String, Object> riderData, ExtractedEntity... entities) {
        when(aiProcessor.process(any(), any())).thenReturn(new EmailProcessingResult(
                new ClassificationResult(EmailClassification.RIDER, 0.96, ClassificationSource.CLAUDE),
                List.of(entities), riderData, null, null));
    }

    private void stubClaudeOtro() {
        when(aiProcessor.process(any(), any())).thenReturn(EmailProcessingResult.classificationOnly(
                new ClassificationResult(EmailClassification.OTRO, 0.90, ClassificationSource.CLAUDE)));
    }

    private void awaitProcessed(String externalId) {
        await().atMost(Duration.ofSeconds(10)).until(() ->
                emailRepo.findByExternalMessageId(externalId)
                        .map(EmailMessage::isProcessed)
                        .orElse(false));
    }

    private Authentication promoterAuth(UUID promoterId) {
        return new UsernamePasswordAuthenticationToken(promoterId.toString(), null,
                List.of(new SimpleGrantedAuthority("ROLE_PROMOTER")));
    }
}

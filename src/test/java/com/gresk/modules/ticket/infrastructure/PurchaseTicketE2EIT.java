package com.gresk.modules.ticket.infrastructure;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.event.domain.port.out.EventRepository;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.modules.ticket.domain.model.PaymentResult;
import com.gresk.modules.ticket.domain.port.out.PaymentGateway;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "jwt.secret=dGVzdHNlY3JldGtleWZvcnRlc3RpbmdwdXJwb3Nlc29ubHlub3Rmb3Jwcm9kdWN0aW9u",
                "jwt.expiration-ms=86400000",
                "spring.flyway.enabled=true",
                "spring.autoconfigure.exclude="
        }
)
@AutoConfigureMockMvc
@Testcontainers
class PurchaseTicketE2EIT {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        PaymentGateway stubPaymentGateway() {
            return (userId, eventId, amount) -> new PaymentResult(true, UUID.randomUUID().toString());
        }
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = UserId.generate();
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(userId, null, "ROLE_USER"));
    }

    @Test
    void happyPath_purchaseTicket_returns201AndDecrementsCapacity() throws Exception {
        Event event = createAndSavePublishedEvent(10);

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\": \"" + event.getId().value() + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("PURCHASED"))
                .andExpect(jsonPath("$.qrCode").isNotEmpty())
                .andExpect(jsonPath("$.purchasedAt").isNotEmpty());

        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(updatedEvent.getCapacity().available()).isEqualTo(9);
    }

    @Test
    void listTickets_afterPurchase_returns200WithTicket() throws Exception {
        Event event = createAndSavePublishedEvent(10);

        mockMvc.perform(post("/api/v1/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"eventId\": \"" + event.getId().value() + "\"}"));

        mockMvc.perform(get("/api/v1/users/me/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("PURCHASED"));
    }

    @Test
    void getQrImage_afterPurchase_returns200WithPng() throws Exception {
        Event event = createAndSavePublishedEvent(10);

        String responseBody = mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\": \"" + event.getId().value() + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String ticketId = com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
                .readTree(responseBody).get("id").asText();

        byte[] body = mockMvc.perform(get("/api/v1/tickets/{id}/qr", ticketId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn().getResponse().getContentAsByteArray();

        // PNG magic bytes: 0x89 P N G
        assertThat(body[0]).isEqualTo((byte) 0x89);
        assertThat(body[1]).isEqualTo((byte) 'P');
    }

    @Test
    void idempotency_secondPurchaseSameUserAndEvent_returns409() throws Exception {
        Event event = createAndSavePublishedEvent(10);

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\": \"" + event.getId().value() + "\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\": \"" + event.getId().value() + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_TICKET"));
    }

    @Test
    void soldOut_secondUserCannotPurchase_returns422() throws Exception {
        Event event = createAndSavePublishedEvent(1);

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\": \"" + event.getId().value() + "\"}"))
                .andExpect(status().isCreated());

        UserId otherUserId = UserId.generate();
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(otherUserId, null, "ROLE_USER"));

        mockMvc.perform(post("/api/v1/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\": \"" + event.getId().value() + "\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("EVENT_SOLD_OUT"));
    }

    private Event createAndSavePublishedEvent(int capacity) {
        Event event = Event.create("E2E Test Event", PromoterId.generate())
                .withGenre(Genre.ELECTRONIC)
                .withPrice(new Price(new BigDecimal("20.00"), "EUR"))
                .withCapacity(Capacity.of(capacity))
                .withEventDate(LocalDateTime.now().plusMonths(1))
                .withLocation(new Location("Madrid", "Gran Via 1", "Sala"));
        event.publish();
        return eventRepository.save(event);
    }
}

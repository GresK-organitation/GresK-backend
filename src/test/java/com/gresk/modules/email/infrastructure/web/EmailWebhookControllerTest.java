package com.gresk.modules.email.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gresk.modules.email.infrastructure.gmail.GmailProperties;
import com.gresk.modules.email.infrastructure.gmail.GmailSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmailWebhookControllerTest {

    private static final String VALID_TOKEN = "pubsub-secret";

    @Mock private GmailSyncService syncService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        GmailProperties properties = new GmailProperties(
                "client-id", "client-secret", "http://localhost/callback", "", VALID_TOKEN);
        mvc = MockMvcBuilders.standaloneSetup(
                new EmailWebhookController(properties, syncService, new ObjectMapper())).build();
    }

    @Test
    void devuelve401SiElTokenPubSubNoEsValido() throws Exception {
        mvc.perform(post("/api/v1/email/gmail/webhook")
                        .param("token", "token-incorrecto")
                        .contentType(APPLICATION_JSON)
                        .content(envelope("promotora@gmail.com", 4711)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(syncService);
    }

    @Test
    void devuelve401SiNoLlegaToken() throws Exception {
        mvc.perform(post("/api/v1/email/gmail/webhook")
                        .contentType(APPLICATION_JSON)
                        .content(envelope("promotora@gmail.com", 4711)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void conTokenValido_decodificaYDisparaLaSincronizacion() throws Exception {
        mvc.perform(post("/api/v1/email/gmail/webhook")
                        .param("token", VALID_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(envelope("promotora@gmail.com", 4711)))
                .andExpect(status().isNoContent());

        verify(syncService).sync("promotora@gmail.com", 4711L);
    }

    @Test
    void unPayloadCorruptoNoProvocaRedeliveries_devuelve204() throws Exception {
        String body = """
                {"message": {"data": "no-es-base64-valido!!", "messageId": "1"}, "subscription": "s"}
                """;

        mvc.perform(post("/api/v1/email/gmail/webhook")
                        .param("token", VALID_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        verifyNoInteractions(syncService);
    }

    private String envelope(String emailAddress, long historyId) {
        String payload = "{\"emailAddress\":\"" + emailAddress + "\",\"historyId\":" + historyId + "}";
        String data = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return "{\"message\": {\"data\": \"" + data + "\", \"messageId\": \"m-1\"}, \"subscription\": \"s\"}";
    }
}

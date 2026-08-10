package com.gresk.modules.email.infrastructure.ai;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Valida la configuración del circuit breaker "claudeApi" (los mismos valores
 * que application.yml) contra una API Claude simulada con WireMock que
 * devuelve errores 5xx: tras 5 fallos consecutivos el breaker abre y las
 * llamadas siguientes se rechazan sin tocar la red.
 */
class ClaudeApiCircuitBreakerTest {

    private WireMockServer wireMock;
    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        wireMock = new WireMockServer(wireMockConfig().dynamicPort());
        wireMock.start();
        wireMock.stubFor(post(urlEqualTo("/v1/messages"))
                .willReturn(aResponse().withStatus(500).withBody("simulated Claude API outage")));

        // Misma configuración que resilience4j.circuitbreaker.instances.claudeApi
        circuitBreaker = CircuitBreaker.of("claudeApi", CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .minimumNumberOfCalls(5)
                .slidingWindowSize(10)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build());
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    @Test
    void elBreakerAbreTras5FallosConsecutivosSimulados() {
        Supplier<String> protectedCall =
                CircuitBreaker.decorateSupplier(circuitBreaker, this::callSimulatedClaudeApi);

        for (int attempt = 1; attempt <= 5; attempt++) {
            assertThrows(IllegalStateException.class, protectedCall::get,
                    "el fallo " + attempt + " debe propagarse mientras el breaker está cerrado");
        }

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        int requestsBeforeRejectedCall = wireMock.getAllServeEvents().size();
        assertThrows(CallNotPermittedException.class, protectedCall::get,
                "con el breaker abierto la llamada se rechaza sin ejecutarse");
        assertEquals(requestsBeforeRejectedCall, wireMock.getAllServeEvents().size(),
                "la llamada rechazada no debe llegar a la API");
    }

    private String callSimulatedClaudeApi() {
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(wireMock.baseUrl() + "/v1/messages"))
                            .header("x-api-key", "test-key")
                            .POST(HttpRequest.BodyPublishers.ofString("{}"))
                            .build(),
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 500) {
                throw new IllegalStateException("Claude API error " + response.statusCode());
            }
            return response.body();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Claude API call failed", e);
        }
    }
}

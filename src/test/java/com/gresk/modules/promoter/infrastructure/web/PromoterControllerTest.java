package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.modules.promoter.application.usecase.AuthenticatePromoterUseCase;
import com.gresk.modules.promoter.application.usecase.GetPromoterUseCase;
import com.gresk.modules.promoter.application.usecase.RegisterPromoterUseCase;
import com.gresk.modules.promoter.application.usecase.UpdatePromoterProfileUseCase;
import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.domain.exception.EmailAlreadyExistsException;
import com.gresk.modules.promoter.domain.exception.InvalidCredentialsException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.*;
import com.gresk.shared.port.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests de capa web para PromoterController.
 *
 * <p>Spring Boot 4 eliminó @WebFluxTest (y todos los test-slices salvo @JsonTest).
 * La alternativa es {@code WebTestClient.bindToController(controller)}, que viene
 * de spring-test 7 y no necesita contexto Spring. Levanta un dispatcher WebFlux
 * mínimo con el controlador y el advice sin ningún bean de infraestructura.
 *
 * <p>Para los endpoints /me se usa {@code SecurityMockServerConfigurers.mockAuthentication},
 * que añade un WebFilter que escribe el Authentication en el ReactiveSecurityContextHolder
 * (vía reactor Context) antes de que el handler se ejecute.
 *
 * <p>El test de "sin autenticación devuelve 401" requiere la SecurityWebFilterChain real
 * (SecurityConfig) y por tanto forma parte de los tests de integración, no de los tests
 * de controlador.
 */
@ExtendWith(MockitoExtension.class)
class PromoterControllerTest {

    @Mock RegisterPromoterUseCase registerUseCase;
    @Mock AuthenticatePromoterUseCase authenticateUseCase;
    @Mock GetPromoterUseCase getUseCase;
    @Mock UpdatePromoterProfileUseCase updateUseCase;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        PromoterController controller = new PromoterController(
                registerUseCase, authenticateUseCase, getUseCase, updateUseCase);
        webTestClient = WebTestClient
                .bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                // Registra el MutatorFilter de Spring Security Test que permite usar
                // mutateWith(mockAuthentication(...)) para inyectar un Authentication
                // en el ReactiveSecurityContextHolder (via reactor Context).
                .apply(SecurityMockServerConfigurers.springSecurity())
                .build();
    }

    // -------------------------------------------------------------------------
    // POST /register
    // -------------------------------------------------------------------------

    @Test
    void register_success_returns201WithId() {
        when(registerUseCase.execute(any()))
                .thenReturn(reactor.core.publisher.Mono.just(PromoterId.generate()));

        webTestClient.post().uri("/api/v1/promoters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "email": "test@gresk.com",
                          "password": "password123",
                          "name": "Club Test",
                          "city": "Madrid",
                          "country": "España"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.id").isNotEmpty();
    }

    @Test
    void register_emailConflict_returns409() {
        when(registerUseCase.execute(any()))
                .thenReturn(reactor.core.publisher.Mono.error(
                        new EmailAlreadyExistsException("test@gresk.com")));

        webTestClient.post().uri("/api/v1/promoters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "email": "test@gresk.com",
                          "password": "password123",
                          "name": "Club Test",
                          "city": "Madrid",
                          "country": "España"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody().jsonPath("$.error").isNotEmpty();
    }

    @Test
    void register_invalidBody_returns400() {
        // email vacío → falla @NotBlank @Email → WebExchangeBindException → 400
        webTestClient.post().uri("/api/v1/promoters/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "email": "",
                          "password": "password123",
                          "name": "Club Test",
                          "city": "Madrid",
                          "country": "España"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isNotEmpty();
    }

    // -------------------------------------------------------------------------
    // POST /login
    // -------------------------------------------------------------------------

    @Test
    void login_success_returns200WithToken() {
        when(authenticateUseCase.execute(any()))
                .thenReturn(reactor.core.publisher.Mono.just(
                        new AuthToken("jwt.token.here", 86400L)));

        webTestClient.post().uri("/api/v1/promoters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"email": "test@gresk.com", "password": "password123"}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo("jwt.token.here")
                .jsonPath("$.expiresIn").isEqualTo(86400);
    }

    @Test
    void login_wrongCredentials_returns401() {
        when(authenticateUseCase.execute(any()))
                .thenReturn(reactor.core.publisher.Mono.error(
                        new InvalidCredentialsException()));

        webTestClient.post().uri("/api/v1/promoters/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"email": "test@gresk.com", "password": "wrongpass"}
                        """)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().jsonPath("$.error").isNotEmpty();
    }

    // -------------------------------------------------------------------------
    // GET /me  — requiere Authentication en el ReactiveSecurityContextHolder
    // -------------------------------------------------------------------------

    @Test
    void getMe_withValidAuth_returns200() {
        PromoterId promoterId = PromoterId.generate();
        when(getUseCase.execute(any()))
                .thenReturn(reactor.core.publisher.Mono.just(buildPromoter(promoterId)));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockAuthentication(
                        new TestingAuthenticationToken(promoterId, null, "ROLE_PROMOTER")
                ))
                .get().uri("/api/v1/promoters/me")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.email").isEqualTo("test@gresk.com")
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    // -------------------------------------------------------------------------
    // PUT /me  — requiere Authentication en el ReactiveSecurityContextHolder
    // -------------------------------------------------------------------------

    @Test
    void updateMe_withValidAuth_returns204() {
        PromoterId promoterId = PromoterId.generate();
        when(updateUseCase.execute(any()))
                .thenReturn(reactor.core.publisher.Mono.empty());

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockAuthentication(
                        new TestingAuthenticationToken(promoterId, null, "ROLE_PROMOTER")
                ))
                .put().uri("/api/v1/promoters/me")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"name": "Club Nuevo", "city": "Barcelona", "country": "España"}
                        """)
                .exchange()
                .expectStatus().isNoContent();
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Promoter buildPromoter(PromoterId id) {
        return Promoter.reconstitute(
                id,
                new Email("test@gresk.com"),
                new Password("$2a$10$hash"),
                new PromoterName("Club Test"),
                null,
                new Location("Madrid", "España", null),
                Set.of(),
                PromoterStatus.ACTIVE,
                LocalDateTime.now(),
                true
        );
    }
}

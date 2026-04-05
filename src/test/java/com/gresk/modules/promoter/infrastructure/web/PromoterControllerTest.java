package com.gresk.modules.promoter.infrastructure.web;

import com.gresk.infrastructure.port.AuthToken;
import com.gresk.modules.promoter.application.port.in.AuthenticatePromoterPort;
import com.gresk.modules.promoter.application.port.in.GetPromoterPort;
import com.gresk.modules.promoter.application.port.in.RegisterPromoterPort;
import com.gresk.modules.promoter.application.port.in.UpdatePromoterProfilePort;
import com.gresk.modules.promoter.domain.PromoterStatus;
import com.gresk.modules.promoter.domain.exception.EmailAlreadyExistsException;
import com.gresk.modules.promoter.domain.exception.InvalidCredentialsException;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de capa web para PromoterController.
 *
 * <p>Usa MockMvc en modo standalone, sin contexto Spring completo.
 * {@code SecurityMockMvcConfigurers.springSecurity()} registra los filtros y
 * resolvers necesarios para que {@code @AuthenticationPrincipal} funcione.
 * Para los endpoints /me se inyecta la autenticación con
 * {@code SecurityMockMvcRequestPostProcessors.authentication(...)}.
 */
@ExtendWith(MockitoExtension.class)
class PromoterControllerTest {

    @Mock RegisterPromoterPort registerUseCase;
    @Mock AuthenticatePromoterPort authenticateUseCase;
    @Mock GetPromoterPort getUseCase;
    @Mock UpdatePromoterProfilePort updateUseCase;

    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setUp() {
        PromoterController controller = new PromoterController(
                registerUseCase, authenticateUseCase, getUseCase, updateUseCase);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    // -------------------------------------------------------------------------
    // POST /register
    // -------------------------------------------------------------------------

    @Test
    void register_success_returns201WithId() throws Exception {
        when(registerUseCase.execute(any())).thenReturn(PromoterId.generate());

        mockMvc.perform(post("/api/v1/promoters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@gresk.com",
                                  "password": "password123",
                                  "name": "Club Test",
                                  "city": "Madrid",
                                  "country": "España"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void register_emailConflict_returns409() throws Exception {
        when(registerUseCase.execute(any()))
                .thenThrow(new EmailAlreadyExistsException("test@gresk.com"));

        mockMvc.perform(post("/api/v1/promoters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@gresk.com",
                                  "password": "password123",
                                  "name": "Club Test",
                                  "city": "Madrid",
                                  "country": "España"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void register_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/promoters/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": "password123",
                                  "name": "Club Test",
                                  "city": "Madrid",
                                  "country": "España"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // POST /login
    // -------------------------------------------------------------------------

    @Test
    void login_success_returns200WithToken() throws Exception {
        when(authenticateUseCase.execute(any()))
                .thenReturn(new AuthToken("jwt.token.here", 86400L));

        mockMvc.perform(post("/api/v1/promoters/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "test@gresk.com", "password": "password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.here"))
                .andExpect(jsonPath("$.expiresIn").value(86400));
    }

    @Test
    void login_wrongCredentials_returns401() throws Exception {
        when(authenticateUseCase.execute(any()))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/promoters/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "test@gresk.com", "password": "wrongpass"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // GET /me  — requiere Authentication en el SecurityContextHolder
    // -------------------------------------------------------------------------

    @Test
    void getMe_withValidAuth_returns200() throws Exception {
        PromoterId promoterId = PromoterId.generate();
        when(getUseCase.execute(any())).thenReturn(buildPromoter(promoterId));

        mockMvc.perform(get("/api/v1/promoters/me")
                        .with(withPrincipal(promoterId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value("test@gresk.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    // -------------------------------------------------------------------------
    // PUT /me  — requiere Authentication en el SecurityContextHolder
    // -------------------------------------------------------------------------

    @Test
    void updateMe_withValidAuth_returns204() throws Exception {
        PromoterId promoterId = PromoterId.generate();

        mockMvc.perform(put("/api/v1/promoters/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Club Nuevo", "city": "Barcelona", "country": "España"}
                                """)
                        .with(withPrincipal(promoterId)))
                .andExpect(status().isNoContent());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static RequestPostProcessor withPrincipal(PromoterId promoterId) {
        return request -> {
            SecurityContextHolder.getContext().setAuthentication(
                    new TestingAuthenticationToken(promoterId, null, "ROLE_PROMOTER"));
            return request;
        };
    }

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

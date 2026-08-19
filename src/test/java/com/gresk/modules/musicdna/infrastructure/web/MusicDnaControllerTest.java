package com.gresk.modules.musicdna.infrastructure.web;

import com.gresk.infrastructure.security.SecurityContextService;
import com.gresk.modules.musicdna.application.usecase.GetMyMusicDnaUseCase;
import com.gresk.modules.musicdna.application.usecase.GetUserMusicDnaUseCase;
import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaSignals;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MusicDnaControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();

    @Mock private GetMyMusicDnaUseCase   getMyMusicDnaUseCase;
    @Mock private GetUserMusicDnaUseCase getUserMusicDnaUseCase;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new MusicDnaController(
                        getMyMusicDnaUseCase, getUserMusicDnaUseCase,
                        new SecurityContextService(), new UserMusicDnaResponseMapper()))
                .setControllerAdvice(new MusicDnaExceptionHandler())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_ID.toString(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    private UserMusicDna dna(UserId userId) {
        MusicDnaSignals signals = new MusicDnaSignals(3, 1, 2, 1, 3, 3, 2, 2, 0,
                LocalDate.now().minusYears(1), Instant.now().minusSeconds(3600));
        return UserMusicDna.calculate(userId, signals);
    }

    @Test
    void getMine_devuelve200ConElAdnPropio() throws Exception {
        when(getMyMusicDnaUseCase.execute(UserId.of(USER_ID))).thenReturn(Optional.of(dna(UserId.of(USER_ID))));

        mvc.perform(get("/api/v1/users/me/music-dna"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summaryPhrase").exists());
    }

    @Test
    void getMine_devuelve204SiAunNoHayDatos() throws Exception {
        when(getMyMusicDnaUseCase.execute(UserId.of(USER_ID))).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/users/me/music-dna"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getOther_devuelve200ConElAdnDelOtroUsuario() throws Exception {
        UUID otherUserId = UUID.randomUUID();
        when(getUserMusicDnaUseCase.execute(UserId.of(otherUserId))).thenReturn(Optional.of(dna(UserId.of(otherUserId))));

        mvc.perform(get("/api/v1/users/" + otherUserId + "/music-dna"))
                .andExpect(status().isOk());
    }

    @Test
    void getOther_devuelve404SiNoTieneAdnCalculado() throws Exception {
        UUID otherUserId = UUID.randomUUID();
        when(getUserMusicDnaUseCase.execute(UserId.of(otherUserId))).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/users/" + otherUserId + "/music-dna"))
                .andExpect(status().isNotFound());
    }
}

package com.gresk.modules.musicdna.application.usecase;

import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Lee el ADN Musical de otro usuario. El ADN es un dato agregado sin
 * contenido individual (no expone reseñas ni comentarios), por lo que se
 * sirve público a cualquier usuario autenticado — {@code user} no tiene
 * hoy ningún concepto de perfil privado/público a nivel de cuenta.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserMusicDnaUseCase {

    private final MusicDnaRepository repository;

    public Optional<UserMusicDna> execute(UserId userId) {
        return repository.findByUserId(userId);
    }
}

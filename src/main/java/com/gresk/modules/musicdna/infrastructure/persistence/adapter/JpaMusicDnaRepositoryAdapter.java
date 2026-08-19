package com.gresk.modules.musicdna.infrastructure.persistence.adapter;

import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.port.out.MusicDnaRepository;
import com.gresk.modules.musicdna.infrastructure.persistence.entity.UserMusicDnaEntity;
import com.gresk.modules.musicdna.infrastructure.persistence.mapper.UserMusicDnaMapper;
import com.gresk.modules.musicdna.infrastructure.persistence.repository.UserMusicDnaJpaRepository;
import com.gresk.modules.user.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMusicDnaRepositoryAdapter implements MusicDnaRepository {

    private final UserMusicDnaJpaRepository repo;
    private final UserMusicDnaMapper        mapper;

    @Override
    @Transactional
    public UserMusicDna save(UserMusicDna dna) {
        UserMusicDnaEntity entity = mapper.toEntity(dna);
        // Upsert por user_id: el agregado siempre genera un id nuevo al
        // recalcularse, así que reusamos el id de la fila existente (si la
        // hay) para que Hibernate haga UPDATE en vez de violar el UNIQUE.
        repo.findByUserId(dna.getUserId().value())
                .ifPresent(existing -> entity.setId(existing.getId()));
        return mapper.toDomain(repo.save(entity));
    }

    @Override
    public Optional<UserMusicDna> findByUserId(UserId userId) {
        return repo.findByUserId(userId.value()).map(mapper::toDomain);
    }
}

package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepository;
import com.gresk.modules.promoter.domain.valueobject.Email;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaPromoterAdapter implements PromoterRepository {

    private final PromoterJpaRepository repo;
    private final PromoterMapper mapper;

    @Override
    public Promoter save(Promoter promoter) {
        return mapper.toDomain(repo.save(mapper.toEntity(promoter)));
    }

    @Override
    public Optional<Promoter> findById(PromoterId id) {
        return repo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Promoter> findByEmail(Email email) {
        return repo.findByEmail(email.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repo.existsByEmail(email.value());
    }

    @Override
    public List<Promoter> findAllActive() {
        return repo.findByActiveTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Promoter> findByGenre(MusicGenre genre) {
        return repo.findByGenresContaining(genre).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

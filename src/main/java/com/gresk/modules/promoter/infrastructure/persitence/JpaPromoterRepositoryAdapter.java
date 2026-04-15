package com.gresk.modules.promoter.infrastructure.persitence;

import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.port.out.PromoterRepositoryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.MusicGenre;
import com.gresk.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaPromoterRepositoryAdapter implements PromoterRepositoryPort {

    private final PromoterJpaRepository repository;
    private final PromoterMapper mapper;

    @Override
    public Promoter save(Promoter promoter) {
        PromoterEntity entity = repository.findById(promoter.getId().value())
                .map(existing -> {
                    existing.updateProfile(
                            promoter.getName().value(),
                            promoter.getDescription().value(),
                            promoter.getAddress().city().value(),
                            promoter.getAddress().country(),
                            promoter.getAddress().street(),
                            promoter.getPhone(),
                            promoter.getWebsite(),
                            promoter.getMusicalGenres()
                    );
                    existing.updateLogo(promoter.getLogoAssetId().value());
                    return existing;
                })
                .orElseGet(() -> mapper.toEntity(promoter));
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Promoter> findById(PromoterId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Promoter> findByEmail(Email email) {
        return repository.findByEmail(email.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }


    @Override
    public List<Promoter> findByGenre(MusicGenre genre) {
        return repository.findByGenresContaining(genre).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}

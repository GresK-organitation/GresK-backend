package com.gresk.modules.promoter.domain.port.out;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Email;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromoterRepository {

    Promoter save(Promoter promoter);
    Optional<Promoter> findById(PromoterId id);
    Optional<Promoter> findByEmail(Email email);
    Optional<Promoter> findByAccountId(UUID accountId);
    boolean existsByEmail(Email email);
    List<Promoter> findAllActive();
    List<Promoter> findByGenre(MusicGenre genre);
}

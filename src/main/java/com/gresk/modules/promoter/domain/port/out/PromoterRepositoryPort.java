package com.gresk.modules.promoter.domain.port.out;

import com.gresk.shared.domain.MusicGenre;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.shared.domain.valueobject.Email;

import java.util.List;
import java.util.Optional;

public interface PromoterRepositoryPort {

    Promoter save(Promoter promoter);
    Optional<Promoter> findById(PromoterId id);
    Optional<Promoter> findByEmail(Email email);
    boolean existsByEmail(Email email);
    List<Promoter> findAllActive();
    List<Promoter> findByGenre(MusicGenre genre);
}

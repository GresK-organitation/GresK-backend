package com.gresk.modules.promoter.domain.port.out;

import com.gresk.modules.promoter.domain.MusicGenre;
import com.gresk.modules.promoter.domain.model.Promoter;
import com.gresk.modules.promoter.domain.valueobject.Email;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;

import java.util.List;
import java.util.Optional;

public interface PromoterRepository {

    Promoter save(Promoter promoter);
    Optional<Promoter> findById(PromoterId id);
    Optional<Promoter> findByEmail(Email email);
    boolean existsByEmail(Email email);
    List<Promoter> findAllActive();
    List<Promoter> findByGenre(MusicGenre genre);
}

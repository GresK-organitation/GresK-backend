package com.gresk.modules.promoter.infrastructure.persitence;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("promoter_genres")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class R2dbcPromoterGenreEntity implements Persistable<UUID> {

    @Id
    @Column ("promoter_id")
    private UUID promoterId;

    @Column ("genre")
    private String genre;

    @Override public UUID getId()    { return promoterId; }
    @Override public boolean isNew() { return true; }

}

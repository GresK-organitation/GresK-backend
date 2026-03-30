package com.gresk.modules.promoter.infrastructure.persitence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table ("promoters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class R2dbcPromoterEntity implements Persistable<UUID> {

    @Id
    private UUID id;
    private String email;

    @Column("password_hash")
    private String password_hash;
    private String name;
    private String description;
    private String city;
    private String country;
    private String address;
    private String status;
    private boolean active;

    @Column("created_at")
    private LocalDateTime created_at;

    @Column("updated_at")
    private LocalDateTime updated_at;

    @Transient
    private boolean isNew;

    @Override public UUID getId()     { return id; }
    @Override public boolean isNew()  { return isNew; }


}

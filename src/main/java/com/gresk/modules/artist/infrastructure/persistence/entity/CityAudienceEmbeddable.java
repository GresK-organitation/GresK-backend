package com.gresk.modules.artist.infrastructure.persistence.entity;

import com.gresk.modules.artist.domain.model.valueobject.TractionSource;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityAudienceEmbeddable {

    @Column(name = "city", length = 150, nullable = false)
    private String city;

    @Column(name = "country", length = 150, nullable = false)
    private String country;

    @Column(name = "audience_score")
    private Integer audienceScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 30, nullable = false)
    private TractionSource source;
}

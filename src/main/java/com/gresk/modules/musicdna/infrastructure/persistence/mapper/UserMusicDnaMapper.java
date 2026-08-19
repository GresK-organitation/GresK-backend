package com.gresk.modules.musicdna.infrastructure.persistence.mapper;

import com.gresk.modules.musicdna.domain.model.DimensionScore;
import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import com.gresk.modules.musicdna.domain.model.UserMusicDnaId;
import com.gresk.modules.musicdna.infrastructure.persistence.entity.UserMusicDnaEntity;
import com.gresk.modules.user.domain.model.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserMusicDnaMapper {

    public UserMusicDna toDomain(UserMusicDnaEntity e) {
        // El ratio bruto no se persiste (solo score+label); al reconstituir
        // se usa el score como rawValue placeholder, ya que solo se necesita
        // durante el cálculo (ver DimensionScore.fromRatio).
        return UserMusicDna.reconstitute(
                UserMusicDnaId.of(e.getId()),
                UserId.of(e.getUserId()),
                new DimensionScore(e.getIntensidadScore(), e.getIntensidadScore(), e.getIntensidadLabel()),
                new DimensionScore(e.getDiversidadScore(), e.getDiversidadScore(), e.getDiversidadLabel()),
                new DimensionScore(e.getCriticidadScore(), e.getCriticidadScore(), e.getCriticidadLabel()),
                new DimensionScore(e.getLocalismoScore(), e.getLocalismoScore(), e.getLocalismoLabel()),
                new DimensionScore(e.getAntiguedadScore(), e.getAntiguedadScore(), e.getAntiguedadLabel()),
                new DimensionScore(e.getAutenticidadScore(), e.getAutenticidadScore(), e.getAutenticidadLabel()),
                e.getSummaryPhrase(),
                e.getCalculatedAt()
        );
    }

    public UserMusicDnaEntity toEntity(UserMusicDna dna) {
        return UserMusicDnaEntity.builder()
                .id(dna.getId().value())
                .userId(dna.getUserId().value())
                .intensidadScore(dna.getIntensidad().score())
                .intensidadLabel(dna.getIntensidad().label())
                .diversidadScore(dna.getDiversidad().score())
                .diversidadLabel(dna.getDiversidad().label())
                .criticidadScore(dna.getCriticidad().score())
                .criticidadLabel(dna.getCriticidad().label())
                .localismoScore(dna.getLocalismo().score())
                .localismoLabel(dna.getLocalismo().label())
                .antiguedadScore(dna.getAntiguedad().score())
                .antiguedadLabel(dna.getAntiguedad().label())
                .autenticidadScore(dna.getAutenticidad().score())
                .autenticidadLabel(dna.getAutenticidad().label())
                .summaryPhrase(dna.getSummaryPhrase())
                .calculatedAt(dna.getCalculatedAt())
                .build();
    }
}

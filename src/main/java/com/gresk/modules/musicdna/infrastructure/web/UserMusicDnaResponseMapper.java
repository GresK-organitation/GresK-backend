package com.gresk.modules.musicdna.infrastructure.web;

import com.gresk.modules.musicdna.domain.model.DimensionScore;
import com.gresk.modules.musicdna.domain.model.UserMusicDna;
import org.springframework.stereotype.Component;

@Component
public class UserMusicDnaResponseMapper {

    public UserMusicDnaResponse toResponse(UserMusicDna dna) {
        return new UserMusicDnaResponse(
                dna.getUserId().toString(),
                toResponse(dna.getIntensidad()),
                toResponse(dna.getDiversidad()),
                toResponse(dna.getCriticidad()),
                toResponse(dna.getLocalismo()),
                toResponse(dna.getAntiguedad()),
                toResponse(dna.getAutenticidad()),
                dna.getSummaryPhrase(),
                dna.getCalculatedAt().toString()
        );
    }

    private DimensionScoreResponse toResponse(DimensionScore score) {
        return new DimensionScoreResponse(score.score(), score.label());
    }
}

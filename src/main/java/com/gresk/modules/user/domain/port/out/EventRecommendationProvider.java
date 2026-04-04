package com.gresk.modules.user.domain.port.out;

import com.gresk.modules.user.domain.model.City;
import com.gresk.modules.user.domain.model.EventRecommendation;
import com.gresk.shared.domain.MusicGenre;

import java.util.List;
import java.util.Set;

public interface EventRecommendationProvider {

    /**
     * @param city   Ciudad donde buscar eventos.
     * @param genres Set de géneros preferidos del usuario para filtrar la relevancia.
     */
    List<EventRecommendation> getTopEvents(City city, Set<MusicGenre> genres);
}

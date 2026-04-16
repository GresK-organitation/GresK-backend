package com.gresk.modules.user.infrastructure.adapters.external;

import com.gresk.shared.domain.valueobject.City;
import com.gresk.modules.user.domain.model.EventRecommendation;
import com.gresk.modules.user.domain.port.out.EventRecommendationProvider;
import com.gresk.shared.domain.MusicGenre;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventRecommendationProviderAdapter implements EventRecommendationProvider {

    private static final List<EventRecommendation> MOCK_EVENTS = List.of(
            new EventRecommendation(
                    "e1",
                    "Summer Rave",
                    "Madrid",
                    LocalDateTime.now().plusDays(2),
                    "https://img.com/e1",
                    "Techno"),
            new EventRecommendation(
                    "e2",
                    "Indie Night",
                    "Barcelona",
                    LocalDateTime.now().plusDays(5),
                    "https://img.com/e2",
                    "Indie"),
            new EventRecommendation(
                    "e3",
                    "Urban Fest",
                    "Valencia",
                    LocalDateTime.now().plusDays(10),
                    "https://img.com/e3",
                    "Trap")
    );

    @Override
    public Set<EventRecommendation> getTopEvents(City city, Set<MusicGenre> musicGenreSet) {
        return MOCK_EVENTS.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.shuffle(list);
                            return list.stream().limit(2).collect(Collectors.toSet());
                        }
                ));
    }
}
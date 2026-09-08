package com.gresk.modules.booking.application.port.in;

import com.gresk.modules.booking.application.query.ArtistTimelineQuery;
import com.gresk.modules.booking.application.query.TimelineEntry;

import java.util.List;

public interface GetArtistTimelineUseCase {
    List<TimelineEntry> execute(ArtistTimelineQuery query);
}

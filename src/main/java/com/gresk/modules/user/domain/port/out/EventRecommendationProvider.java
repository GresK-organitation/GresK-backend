package com.gresk.modules.user.domain.port.out;

import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.user.domain.model.EventRecommendation;
import com.gresk.modules.user.domain.model.UserId;
import java.util.List;

public interface EventRecommendationProvider {
    List<EventRecommendation> getTopEventsForUser(UserId id);
}

package com.gresk.modules.rider.domain.model;

import java.util.Set;

public enum RiderItemCategory {
    SOUND_PA,
    MICROPHONE,
    BACKLINE,
    LIGHTING,
    STAGE,
    CATERING,
    DRESSING_ROOM,
    DIET,
    ACCOMMODATION,
    TRANSPORT,
    OTHER;

    private static final Set<RiderItemCategory> TECHNICAL =
            Set.of(SOUND_PA, MICROPHONE, BACKLINE, LIGHTING, STAGE);

    public boolean isTechnical() {
        return TECHNICAL.contains(this);
    }
}

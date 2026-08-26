package com.gresk.modules.discovery.infrastructure.adapter;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.discovery.domain.port.out.CommunitySignals;
import com.gresk.modules.discovery.domain.port.out.CommunitySignalsPort;
import com.gresk.modules.discovery.infrastructure.persistence.CommunitySignalsQueryRepository;
import com.gresk.modules.discovery.infrastructure.persistence.CommunitySignalsRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunitySignalsAdapter implements CommunitySignalsPort {

    private final CommunitySignalsQueryRepository queryRepository;

    @Override
    public CommunitySignals findSignals(ArtistId artistId) {
        CommunitySignalsRow row = queryRepository.findReviewSignals(artistId.value());
        long knownBy = queryRepository.countKnownBy(artistId.value());
        long reviewCount = row.getReviewCount() != null ? row.getReviewCount() : 0;
        long verifiedAttendees = row.getVerifiedAttendeesCount() != null ? row.getVerifiedAttendeesCount() : 0;
        return new CommunitySignals(reviewCount, verifiedAttendees, knownBy);
    }
}

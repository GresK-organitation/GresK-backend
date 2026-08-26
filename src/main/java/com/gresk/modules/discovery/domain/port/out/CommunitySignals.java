package com.gresk.modules.discovery.domain.port.out;

public record CommunitySignals(
        long reviewCount,
        long verifiedAttendeesCount,
        long knownByCount
) {
}

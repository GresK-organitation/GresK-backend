package com.gresk.modules.artist.infrastructure.web;

import java.util.List;

public record BandMemberResponse(
        String id,
        String artistId,
        String name,
        String roleInBand,
        boolean active,
        List<IdentityDocumentResponse> documents
) {}

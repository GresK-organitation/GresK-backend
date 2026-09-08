package com.gresk.modules.logistics.application.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record CrewMemberResponse(String id, String promoterId, String name, String defaultRole, String contactPhone,
                                  String contactEmail, List<DocumentView> documents, boolean active,
                                  Instant createdAt, Instant updatedAt) {

    public record DocumentView(String type, String documentNumber, String issuingCountry, LocalDate expiryDate) {
    }
}

package com.gresk.modules.logistics.application.command;

import java.time.LocalDate;

public record IdentityDocumentInput(String type, String documentNumber, String issuingCountry, LocalDate expiryDate) {
}

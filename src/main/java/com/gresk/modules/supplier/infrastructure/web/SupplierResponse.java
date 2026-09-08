package com.gresk.modules.supplier.infrastructure.web;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record SupplierResponse(
        String id,
        String promoterId,
        String name,
        Set<String> specialties,
        String contactName,
        String contactEmail,
        String contactPhone,
        String serviceCity,
        boolean active,
        List<CatalogItemResponse> catalog,
        Instant createdAt
) {}

package com.gresk.modules.supplier.application.command;

import java.util.Set;

public record RegisterSupplierCommand(
        String promoterId,
        String name,
        Set<String> specialties,
        String contactName,
        String contactEmail,
        String contactPhone,
        String serviceCity
) {}

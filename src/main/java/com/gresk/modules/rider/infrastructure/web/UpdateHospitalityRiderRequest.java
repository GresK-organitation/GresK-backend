package com.gresk.modules.rider.infrastructure.web;

public record UpdateHospitalityRiderRequest(
        String name,
        String additionalNotes
) {}

package com.gresk.modules.logistics.infrastructure.web.dto;

import com.gresk.modules.logistics.application.command.TourLegInput;

import java.util.List;

public record UpdateTourLegsRequest(List<TourLegInput> legs) {
}

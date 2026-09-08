package com.gresk.modules.logistics.infrastructure.web.dto;

import com.gresk.modules.logistics.application.command.EmergencyContactInput;
import com.gresk.modules.logistics.application.command.PointOfInterestInput;

import java.util.List;

public record UpdateTourReferenceDataRequest(List<EmergencyContactInput> emergencyContacts,
                                              List<PointOfInterestInput> pointsOfInterest) {
}

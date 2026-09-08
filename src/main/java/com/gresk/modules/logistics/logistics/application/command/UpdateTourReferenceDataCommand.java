package com.gresk.modules.logistics.application.command;

import java.util.List;

public record UpdateTourReferenceDataCommand(String tourId, String promoterId,
                                              List<EmergencyContactInput> emergencyContacts,
                                              List<PointOfInterestInput> pointsOfInterest) {
}

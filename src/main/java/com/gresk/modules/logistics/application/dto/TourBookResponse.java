package com.gresk.modules.logistics.application.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Vista compilada del Tour Book: agenda día a día de un Tour, lista para render en PDF
 * o en la vista responsive del móvil del Tour Manager. No es un aggregate persistido,
 * se recompone en cada consulta a partir de Tour + TravelParty + Itinerary + RoomingList
 * y del day sheet de cada Booking vía BookingLogisticsQueryPort.
 */
public record TourBookResponse(String tourId, String tourName, String artistId, LocalDate startDate,
                                LocalDate endDate, List<EmergencyContactView> emergencyContacts,
                                List<PointOfInterestView> pointsOfInterest, List<DocumentAlertView> documentAlerts,
                                List<DayView> days) {

    public record EmergencyContactView(String name, String role, String phone, String notes) {
    }

    public record PointOfInterestView(String name, String category, String address, Double latitude,
                                       Double longitude, String notes) {
    }

    /** Documento (pasaporte/visado) de un miembro del travel party que caduca antes del fin de la gira. */
    public record DocumentAlertView(String travelerName, String documentType, LocalDate expiryDate) {
    }

    public record DayView(LocalDate date, String venueName, String venueCity, List<ScheduleLineView> schedule,
                           List<TransportLineView> transport, HotelSummaryView hotel) {
    }

    public record ScheduleLineView(LocalTime time, String type, String label, String notes) {
    }

    public record TransportLineView(String type, Instant departureAt, String departureLocation, Instant arrivalAt,
                                     String arrivalLocation, String carrierOrOperator, String segmentCode,
                                     String confirmationReference, List<String> travelerNames) {
    }

    public record HotelSummaryView(String hotelName, String hotelAddress, LocalDate checkInDate, LocalDate checkOutDate) {
    }
}

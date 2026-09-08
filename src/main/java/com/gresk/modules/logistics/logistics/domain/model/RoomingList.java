package com.gresk.modules.logistics.domain.model;

import com.gresk.modules.logistics.domain.exception.InvalidRoomingListException;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAllotment;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAssignment;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Aggregate root de una estancia de hotel de un Tour: el bloque de habitaciones
 * contratado (cupos por tipo) y su reparto entre el travel party. El reparto puede
 * generarse automáticamente (RoomingListGenerator) o sobrescribirse a mano; en
 * ambos casos pasa por applyAssignments, que valida cupos y evita duplicados.
 */
public final class RoomingList {

    private final RoomingListId id;
    private final TourId tourId;
    private final PromoterId promoterId;
    private final Instant createdAt;

    private String hotelName;
    private String hotelAddress;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<RoomAllotment> allotments;
    private List<RoomAssignment> assignments;
    private Instant updatedAt;

    private RoomingList(RoomingListId id, TourId tourId, PromoterId promoterId, Instant createdAt, String hotelName,
                         String hotelAddress, LocalDate checkInDate, LocalDate checkOutDate,
                         List<RoomAllotment> allotments, List<RoomAssignment> assignments, Instant updatedAt) {
        this.id = id;
        this.tourId = tourId;
        this.promoterId = promoterId;
        this.createdAt = createdAt;
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.allotments = allotments != null ? new ArrayList<>(allotments) : new ArrayList<>();
        this.assignments = assignments != null ? new ArrayList<>(assignments) : new ArrayList<>();
        this.updatedAt = updatedAt;
    }

    public static RoomingList create(TourId tourId, PromoterId promoterId, String hotelName, String hotelAddress,
                                      LocalDate checkInDate, LocalDate checkOutDate, List<RoomAllotment> allotments) {
        validateCore(hotelName, checkInDate, checkOutDate);
        Instant now = Instant.now();
        return new RoomingList(RoomingListId.generate(), tourId, promoterId, now, hotelName.trim(), hotelAddress,
                checkInDate, checkOutDate, allotments, List.of(), now);
    }

    public static RoomingList reconstitute(RoomingListId id, TourId tourId, PromoterId promoterId, Instant createdAt,
                                            String hotelName, String hotelAddress, LocalDate checkInDate,
                                            LocalDate checkOutDate, List<RoomAllotment> allotments,
                                            List<RoomAssignment> assignments, Instant updatedAt) {
        return new RoomingList(id, tourId, promoterId, createdAt, hotelName, hotelAddress, checkInDate, checkOutDate,
                allotments, assignments, updatedAt);
    }

    public void updateDetails(String hotelName, String hotelAddress, LocalDate checkInDate, LocalDate checkOutDate) {
        validateCore(hotelName, checkInDate, checkOutDate);
        this.hotelName = hotelName.trim();
        this.hotelAddress = hotelAddress;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        touch();
    }

    /** Cambiar el bloque invalida el reparto vigente: hay que regenerarlo o reasignar a mano. */
    public void updateAllotments(List<RoomAllotment> newAllotments) {
        this.allotments = new ArrayList<>(newAllotments == null ? List.of() : newAllotments);
        this.assignments = new ArrayList<>();
        touch();
    }

    /** Sobrescribe el reparto completo, generado por RoomingListGenerator o editado a mano. */
    public void applyAssignments(List<RoomAssignment> newAssignments) {
        List<RoomAssignment> candidates = newAssignments == null ? List.of() : newAssignments;
        validateAgainstAllotments(candidates);
        validateNoDuplicateOccupants(candidates);
        this.assignments = new ArrayList<>(candidates);
        touch();
    }

    private void validateAgainstAllotments(List<RoomAssignment> candidates) {
        Map<RoomType, Long> used = new EnumMap<>(RoomType.class);
        for (RoomAssignment a : candidates) {
            if (a.occupantIds().size() > a.roomType().maxOccupancy()) {
                throw new InvalidRoomingListException("Room " + a.id() + " exceeds max occupancy for " + a.roomType());
            }
            used.merge(a.roomType(), 1L, Long::sum);
        }
        for (RoomAllotment allotment : allotments) {
            long usedCount = used.getOrDefault(allotment.roomType(), 0L);
            if (usedCount > allotment.quantity()) {
                throw new InvalidRoomingListException(
                        "Assignments use " + usedCount + " rooms of type " + allotment.roomType()
                                + " but only " + allotment.quantity() + " are allotted");
            }
        }
    }

    private void validateNoDuplicateOccupants(List<RoomAssignment> candidates) {
        List<UUID> allOccupants = candidates.stream().flatMap(a -> a.occupantIds().stream()).toList();
        if (allOccupants.stream().distinct().count() != allOccupants.size()) {
            throw new InvalidRoomingListException("A travel party member cannot occupy more than one room");
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private static void validateCore(String hotelName, LocalDate checkInDate, LocalDate checkOutDate) {
        if (hotelName == null || hotelName.isBlank()) throw new InvalidRoomingListException("hotelName must not be blank");
        if (checkInDate == null) throw new InvalidRoomingListException("checkInDate must not be null");
        if (checkOutDate == null) throw new InvalidRoomingListException("checkOutDate must not be null");
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new InvalidRoomingListException("checkOutDate must be after checkInDate");
        }
    }

    public RoomingListId getId() { return id; }
    public TourId getTourId() { return tourId; }
    public PromoterId getPromoterId() { return promoterId; }
    public Instant getCreatedAt() { return createdAt; }
    public String getHotelName() { return hotelName; }
    public String getHotelAddress() { return hotelAddress; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public List<RoomAllotment> getAllotments() { return List.copyOf(allotments); }
    public List<RoomAssignment> getAssignments() { return List.copyOf(assignments); }
    public Instant getUpdatedAt() { return updatedAt; }
}

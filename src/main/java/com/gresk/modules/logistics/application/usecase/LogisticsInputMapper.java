package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.artist.domain.model.valueobject.IdentityDocument;
import com.gresk.modules.artist.domain.model.valueobject.IdentityDocumentType;
import com.gresk.modules.logistics.application.command.EmergencyContactInput;
import com.gresk.modules.logistics.application.command.IdentityDocumentInput;
import com.gresk.modules.logistics.application.command.IndividualNeedInput;
import com.gresk.modules.logistics.application.command.ItinerarySegmentInput;
import com.gresk.modules.logistics.application.command.PointOfInterestInput;
import com.gresk.modules.logistics.application.command.RoomAllotmentInput;
import com.gresk.modules.logistics.application.command.RoomAssignmentInput;
import com.gresk.modules.logistics.application.command.RoomingPreferenceInput;
import com.gresk.modules.logistics.application.command.TourLegInput;
import com.gresk.modules.logistics.application.command.TravelPartyMemberInput;
import com.gresk.modules.logistics.domain.model.NeedType;
import com.gresk.modules.logistics.domain.model.POICategory;
import com.gresk.modules.logistics.domain.model.PersonType;
import com.gresk.modules.logistics.domain.model.RoomType;
import com.gresk.modules.logistics.domain.model.SegmentType;
import com.gresk.modules.logistics.domain.model.TravelRole;
import com.gresk.modules.logistics.domain.model.valueobject.EmergencyContact;
import com.gresk.modules.logistics.domain.model.valueobject.IndividualNeed;
import com.gresk.modules.logistics.domain.model.valueobject.ItinerarySegment;
import com.gresk.modules.logistics.domain.model.valueobject.PersonRef;
import com.gresk.modules.logistics.domain.model.valueobject.PointOfInterest;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAllotment;
import com.gresk.modules.logistics.domain.model.valueobject.RoomAssignment;
import com.gresk.modules.logistics.domain.model.valueobject.RoomingPreference;
import com.gresk.modules.logistics.domain.model.valueobject.TourLeg;
import com.gresk.modules.logistics.domain.model.valueobject.TravelPartyMember;
import com.gresk.shared.domain.valueobject.Coordinates;
import com.gresk.shared.domain.valueobject.Money;
import com.gresk.shared.domain.valueobject.Name;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Conversión de los "Input" de la capa de aplicación a value objects de dominio. */
final class LogisticsInputMapper {

    private LogisticsInputMapper() {}

    static List<TourLeg> toTourLegs(List<TourLegInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> TourLeg.of(UUID.fromString(i.bookingId()), i.sequenceOrder(), i.showDate(), i.venueName(), i.venueCity()))
                .toList();
    }

    static List<EmergencyContact> toEmergencyContacts(List<EmergencyContactInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> new EmergencyContact(i.name(), i.role(), i.phone(), i.notes()))
                .toList();
    }

    static List<PointOfInterest> toPointsOfInterest(List<PointOfInterestInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream().map(LogisticsInputMapper::toPointOfInterest).toList();
    }

    private static PointOfInterest toPointOfInterest(PointOfInterestInput i) {
        Coordinates coordinates = (i.latitude() != null && i.longitude() != null)
                ? Coordinates.of(i.latitude(), i.longitude()) : null;
        return new PointOfInterest(i.name(), POICategory.valueOf(i.category()), i.address(), coordinates, i.notes());
    }

    static List<IndividualNeed> toIndividualNeeds(List<IndividualNeedInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream().map(i -> new IndividualNeed(NeedType.valueOf(i.type()), i.description())).toList();
    }

    static RoomingPreference toRoomingPreference(RoomingPreferenceInput input) {
        if (input == null) return RoomingPreference.none();
        RoomType preferred = input.preferredRoomType() == null ? null : RoomType.valueOf(input.preferredRoomType());
        UUID preferredRoommateId = input.preferredRoommateId() == null ? null : UUID.fromString(input.preferredRoommateId());
        List<UUID> doNotShareWith = input.doNotShareWith() == null ? List.of()
                : input.doNotShareWith().stream().map(UUID::fromString).toList();
        return new RoomingPreference(preferred, preferredRoommateId, doNotShareWith);
    }

    static List<TravelPartyMember> toTravelPartyMembers(List<TravelPartyMemberInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream().map(LogisticsInputMapper::toTravelPartyMember).toList();
    }

    private static TravelPartyMember toTravelPartyMember(TravelPartyMemberInput i) {
        UUID id = i.id() == null ? null : UUID.fromString(i.id());
        PersonRef personRef = new PersonRef(PersonType.valueOf(i.personType()), UUID.fromString(i.personId()));
        boolean active = i.active() == null || i.active();
        return new TravelPartyMember(id, personRef, i.displayName(), TravelRole.valueOf(i.role()),
                toIndividualNeeds(i.needs()), toRoomingPreference(i.roomingPreference()), active);
    }

    static List<IdentityDocument> toIdentityDocuments(List<IdentityDocumentInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> IdentityDocument.of(IdentityDocumentType.valueOf(i.type()), i.documentNumber(),
                        i.issuingCountry(), i.expiryDate()))
                .toList();
    }

    static List<ItinerarySegment> toItinerarySegments(List<ItinerarySegmentInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream().map(LogisticsInputMapper::toItinerarySegment).toList();
    }

    private static ItinerarySegment toItinerarySegment(ItinerarySegmentInput i) {
        UUID id = i.id() == null ? null : UUID.fromString(i.id());
        Set<UUID> travelerIds = i.travelerIds() == null ? Set.of()
                : i.travelerIds().stream().map(UUID::fromString).collect(Collectors.toSet());
        return new ItinerarySegment(id, SegmentType.valueOf(i.type()), i.departureAt(), i.departureLocation(),
                i.arrivalAt(), i.arrivalLocation(), i.carrierOrOperator(), i.segmentCode(), i.confirmationReference(),
                i.seatOrCapacityInfo(), i.voucherUrl(), i.notes(), travelerIds);
    }

    static List<RoomAllotment> toRoomAllotments(List<RoomAllotmentInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream()
                .map(i -> new RoomAllotment(RoomType.valueOf(i.roomType()), i.quantity(),
                        Money.of(i.costAmount(), i.currency())))
                .toList();
    }

    static List<RoomAssignment> toRoomAssignments(List<RoomAssignmentInput> inputs) {
        if (inputs == null) return List.of();
        return inputs.stream().map(LogisticsInputMapper::toRoomAssignment).toList();
    }

    private static RoomAssignment toRoomAssignment(RoomAssignmentInput i) {
        UUID id = i.id() == null ? null : UUID.fromString(i.id());
        List<UUID> occupantIds = i.occupantIds().stream().map(UUID::fromString).toList();
        return new RoomAssignment(id, RoomType.valueOf(i.roomType()), occupantIds, i.roomNumber());
    }

    static Name toName(String value) {
        return Name.of(value);
    }
}

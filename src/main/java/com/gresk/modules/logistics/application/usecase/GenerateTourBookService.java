package com.gresk.modules.logistics.application.usecase;

import com.gresk.modules.logistics.application.dto.TourBookResponse;
import com.gresk.modules.logistics.application.port.in.GenerateTourBookUseCase;
import com.gresk.modules.logistics.application.query.GenerateTourBookQuery;
import com.gresk.modules.logistics.domain.model.CrewMember;
import com.gresk.modules.logistics.domain.model.CrewMemberId;
import com.gresk.modules.logistics.domain.model.Itinerary;
import com.gresk.modules.logistics.domain.model.PersonType;
import com.gresk.modules.logistics.domain.model.RoomingList;
import com.gresk.modules.logistics.domain.model.Tour;
import com.gresk.modules.logistics.domain.model.TourId;
import com.gresk.modules.logistics.domain.model.TravelParty;
import com.gresk.modules.logistics.domain.model.valueobject.ItinerarySegment;
import com.gresk.modules.logistics.domain.model.valueobject.TourLeg;
import com.gresk.modules.logistics.domain.model.valueobject.TravelPartyMember;
import com.gresk.modules.logistics.domain.port.out.BandMemberLogisticsQueryPort;
import com.gresk.modules.logistics.domain.port.out.BandMemberLogisticsQueryPort.BandMemberLogisticsView;
import com.gresk.modules.logistics.domain.port.out.BookingLogisticsQueryPort;
import com.gresk.modules.logistics.domain.port.out.BookingLogisticsQueryPort.BookingLogisticsView;
import com.gresk.modules.logistics.domain.port.out.CrewMemberRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.ItineraryRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.RoomingListRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.TourRepositoryPort;
import com.gresk.modules.logistics.domain.port.out.TravelPartyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Compila el Tour Book de un Tour: agenda día a día combinando el day sheet de cada
 * Booking (booking.DaySheet, vía puerto anti-corrupción), los tramos de transporte del
 * Itinerary, el hotel vigente según el RoomingList y alertas de documentación caducada.
 * No persiste nada: es una vista de solo lectura recompuesta en cada llamada.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerateTourBookService implements GenerateTourBookUseCase {

    private final TourRepositoryPort tourRepository;
    private final TravelPartyRepositoryPort travelPartyRepository;
    private final ItineraryRepositoryPort itineraryRepository;
    private final RoomingListRepositoryPort roomingListRepository;
    private final CrewMemberRepositoryPort crewMemberRepository;
    private final BookingLogisticsQueryPort bookingLogisticsQueryPort;
    private final BandMemberLogisticsQueryPort bandMemberLogisticsQueryPort;

    @Override
    public TourBookResponse execute(GenerateTourBookQuery query) {
        Tour tour = LogisticsLookups.requireTour(tourRepository, query.tourId(), query.promoterId());
        TourId tourId = tour.getId();

        List<TravelPartyMember> members = travelPartyRepository.findByTourId(tourId)
                .map(TravelParty::getActiveMembers).orElse(List.of());
        Map<UUID, String> memberNames = members.stream()
                .collect(Collectors.toMap(TravelPartyMember::id, TravelPartyMember::displayName));

        List<ItinerarySegment> segments = itineraryRepository.findByTourId(tourId)
                .map(Itinerary::getSegments).orElse(List.of());

        List<RoomingList> roomingLists = roomingListRepository.findAllByTourId(tourId);

        Set<UUID> bookingIds = tour.getLegs().stream().map(TourLeg::bookingId).collect(Collectors.toSet());
        Map<UUID, BookingLogisticsView> bookingViews = bookingLogisticsQueryPort.findViews(bookingIds);

        List<TourBookResponse.DayView> days = tour.getLegs().stream()
                .map(leg -> toDayView(leg, bookingViews.get(leg.bookingId()), segments, roomingLists, memberNames))
                .toList();

        return new TourBookResponse(
                tourId.toString(),
                tour.getName(),
                tour.getArtistId().toString(),
                tour.getStartDate(),
                tour.getEndDate(),
                tour.getEmergencyContacts().stream()
                        .map(c -> new TourBookResponse.EmergencyContactView(c.name(), c.role(), c.phone(), c.notes()))
                        .toList(),
                tour.getPointsOfInterest().stream()
                        .map(p -> new TourBookResponse.PointOfInterestView(p.name(), p.category().name(), p.address(),
                                p.coordinates() == null ? null : p.coordinates().latitude(),
                                p.coordinates() == null ? null : p.coordinates().longitude(), p.notes()))
                        .toList(),
                buildDocumentAlerts(members, tour.getEndDate()),
                days);
    }

    private TourBookResponse.DayView toDayView(TourLeg leg, BookingLogisticsView bookingView,
                                                List<ItinerarySegment> segments, List<RoomingList> roomingLists,
                                                Map<UUID, String> memberNames) {
        List<TourBookResponse.ScheduleLineView> schedule = bookingView == null ? List.of()
                : bookingView.daySheetLines().stream()
                        .map(l -> new TourBookResponse.ScheduleLineView(l.time(), l.type(), l.label(), l.notes()))
                        .toList();

        List<TourBookResponse.TransportLineView> transport = segments.stream()
                .filter(s -> overlapsDay(s, leg.showDate()))
                .map(s -> toTransportLineView(s, memberNames))
                .toList();

        TourBookResponse.HotelSummaryView hotel = roomingLists.stream()
                .filter(r -> !leg.showDate().isBefore(r.getCheckInDate()) && leg.showDate().isBefore(r.getCheckOutDate()))
                .findFirst()
                .map(r -> new TourBookResponse.HotelSummaryView(r.getHotelName(), r.getHotelAddress(),
                        r.getCheckInDate(), r.getCheckOutDate()))
                .orElse(null);

        String venueName = bookingView != null ? bookingView.venueName() : leg.venueName();
        String venueCity = bookingView != null ? bookingView.venueCity() : leg.venueCity();
        return new TourBookResponse.DayView(leg.showDate(), venueName, venueCity, schedule, transport, hotel);
    }

    private boolean overlapsDay(ItinerarySegment segment, LocalDate day) {
        LocalDate departureDate = segment.departureAt().atZone(ZoneOffset.UTC).toLocalDate();
        if (departureDate.equals(day)) return true;
        if (segment.arrivalAt() == null) return false;
        return segment.arrivalAt().atZone(ZoneOffset.UTC).toLocalDate().equals(day);
    }

    private TourBookResponse.TransportLineView toTransportLineView(ItinerarySegment s, Map<UUID, String> memberNames) {
        List<String> travelerNames = s.travelerIds().stream()
                .map(id -> memberNames.getOrDefault(id, id.toString()))
                .toList();
        return new TourBookResponse.TransportLineView(s.type().name(), s.departureAt(), s.departureLocation(),
                s.arrivalAt(), s.arrivalLocation(), s.carrierOrOperator(), s.segmentCode(),
                s.confirmationReference(), travelerNames);
    }

    /** Cruza los documentos de identidad de músicos (artist.BandMember) y crew propio contra el fin de la gira. */
    private List<TourBookResponse.DocumentAlertView> buildDocumentAlerts(List<TravelPartyMember> members, LocalDate tourEndDate) {
        Set<UUID> bandMemberIds = members.stream()
                .filter(m -> m.personRef().type() == PersonType.BAND_MEMBER)
                .map(m -> m.personRef().personId())
                .collect(Collectors.toSet());
        Set<UUID> crewIds = members.stream()
                .filter(m -> m.personRef().type() == PersonType.CREW)
                .map(m -> m.personRef().personId())
                .collect(Collectors.toSet());

        Map<UUID, BandMemberLogisticsView> bandViews = bandMemberLogisticsQueryPort.findViews(bandMemberIds);
        List<CrewMember> crewMembers = crewMemberRepository.findAllByIds(crewIds.stream().map(CrewMemberId::of).toList());

        Map<UUID, String> displayNameByPersonId = new HashMap<>();
        for (TravelPartyMember m : members) {
            displayNameByPersonId.put(m.personRef().personId(), m.displayName());
        }

        List<TourBookResponse.DocumentAlertView> alerts = new ArrayList<>();
        for (BandMemberLogisticsView view : bandViews.values()) {
            String name = displayNameByPersonId.getOrDefault(view.bandMemberId(), view.name());
            for (var doc : view.documents()) {
                if (doc.expiryDate() != null && !doc.expiryDate().isAfter(tourEndDate)) {
                    alerts.add(new TourBookResponse.DocumentAlertView(name, doc.type(), doc.expiryDate()));
                }
            }
        }
        for (CrewMember crew : crewMembers) {
            String name = displayNameByPersonId.getOrDefault(crew.getId().value(), crew.getName().value());
            for (var doc : crew.getDocuments()) {
                if (doc.expiryDate() != null && !doc.expiryDate().isAfter(tourEndDate)) {
                    alerts.add(new TourBookResponse.DocumentAlertView(name, doc.type().name(), doc.expiryDate()));
                }
            }
        }
        return alerts;
    }
}

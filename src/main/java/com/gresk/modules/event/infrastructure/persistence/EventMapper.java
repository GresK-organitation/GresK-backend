package com.gresk.modules.event.infrastructure.persistence;

import com.gresk.modules.event.domain.model.*;
import com.gresk.modules.promoter.domain.valueobject.PromoterId;
import io.r2dbc.spi.Row;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class EventMapper {

    private EventMapper() {}

    public static Event toDomain(EventEntity entity) {
        Price price = entity.getAmount() != null
                ? new Price(entity.getAmount(), entity.getCurrency()) : null;

        Capacity capacity = entity.getTotalCapacity() != null
                ? new Capacity(entity.getTotalCapacity(), entity.getAvailableCapacity()) : null;

        Location location = entity.getCity() != null
                ? new Location(entity.getCity(), entity.getAddress(), entity.getVenue()) : null;

        Genre genre = entity.getGenre() != null
                ? Genre.valueOf(entity.getGenre()) : null;

        return Event.reconstitute(
                EventId.of(entity.getId().toString()),
                entity.getTitle(),
                PromoterId.of(entity.getPromoterId().toString()),
                genre,
                price,
                capacity,
                toLocalDateTime(entity.getEventDate()),
                location,
                toLocalDateTime(entity.getRevealAt()),
                EventStatus.valueOf(entity.getStatus()),
                toLocalDateTime(entity.getCreatedAt())
        );
    }

    public static Event fromRow(Row row) {
        String genreVal = row.get("genre", String.class);
        Genre genre = genreVal != null ? Genre.valueOf(genreVal) : null;

        BigDecimal amount = row.get("amount", BigDecimal.class);
        String currency   = row.get("currency", String.class);
        Price price = amount != null ? new Price(amount, currency) : null;

        Integer total     = row.get("total_capacity", Integer.class);
        Integer available = row.get("available_capacity", Integer.class);
        Capacity capacity = total != null ? new Capacity(total, available) : null;

        String city    = row.get("city", String.class);
        String address = row.get("address", String.class);
        String venue   = row.get("venue", String.class);
        Location location = city != null ? new Location(city, address, venue) : null;

        return Event.reconstitute(
                EventId.of(row.get("id", UUID.class).toString()),
                row.get("title", String.class),
                PromoterId.of(row.get("promoter_id", UUID.class).toString()),
                genre,
                price,
                capacity,
                toLocalDateTime(row.get("event_date", OffsetDateTime.class)),
                location,
                toLocalDateTime(row.get("reveal_at", OffsetDateTime.class)),
                EventStatus.valueOf(row.get("status", String.class)),
                toLocalDateTime(row.get("created_at", OffsetDateTime.class))
        );
    }

    public static EventEntity toEntity(Event event) {
        EventEntity e = new EventEntity();
        e.setId(event.getId().value());
        e.setTitle(event.getTitle());
        e.setPromoterId(event.getPromoterId().value());
        e.setStatus(event.getStatus().name());
        e.setGenre(event.getGenre() != null ? event.getGenre().name() : null);

        if (event.getPrice() != null) {
            e.setAmount(event.getPrice().amount());
            e.setCurrency(event.getPrice().currency());
        }
        if (event.getCapacity() != null) {
            e.setTotalCapacity(event.getCapacity().total());
            e.setAvailableCapacity(event.getCapacity().available());
        }
        if (event.getLocation() != null) {
            e.setCity(event.getLocation().city());
            e.setAddress(event.getLocation().address());
            e.setVenue(event.getLocation().venue());
        }

        e.setEventDate(toOffsetDateTime(event.getEventDate()));
        e.setRevealAt(toOffsetDateTime(event.getRevealAt()));
        e.setCreatedAt(toOffsetDateTime(event.getCreatedAt()));
        e.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return e;
    }

    private static LocalDateTime toLocalDateTime(OffsetDateTime odt) {
        return odt != null ? odt.toLocalDateTime() : null;
    }

    private static OffsetDateTime toOffsetDateTime(LocalDateTime ldt) {
        return ldt != null ? ldt.atOffset(ZoneOffset.UTC) : null;
    }
}

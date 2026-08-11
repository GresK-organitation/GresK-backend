package com.gresk.modules.journal.domain.model;

import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.journal.domain.exception.InvalidJournalEntryException;
import com.gresk.modules.journal.domain.exception.TooManyMediaItemsException;
import com.gresk.modules.user.domain.model.UserId;
import com.gresk.shared.domain.valueobject.AssetId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JournalEntryTest {

    private final UserId userId = UserId.of(UUID.randomUUID());

    @Test
    void creaUnaEntradaConArtistaEnTextoLibre() {
        JournalEntry entry = JournalEntry.create(userId, "Radiohead", null,
                ApproxDate.ofYear(2019), "Sala Apolo", "Barcelona", null,
                "Concierto inolvidable", List.of(RatingCriterion.of("luces", 5)),
                null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);

        assertEquals("Radiohead", entry.getArtistNameFree());
        assertEquals(JournalVisibility.PRIVATE, entry.getVisibility());
        assertEquals(JournalEntrySource.MANUAL, entry.getSource());
        assertTrue(entry.getMedia().isEmpty());
    }

    @Test
    void creaUnaEntradaConArtistaDelCatalogo() {
        JournalEntry entry = JournalEntry.create(userId, null, ArtistId.generate(),
                ApproxDate.ofYear(2019), null, null, null,
                null, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);

        assertEquals(null, entry.getArtistNameFree());
        assertTrue(entry.getArtistId() != null);
    }

    @Test
    void rechazaEntradaSinArtistaLibreNiDelCatalogo() {
        assertThrows(InvalidJournalEntryException.class, () ->
                JournalEntry.create(userId, null, null,
                        ApproxDate.ofYear(2019), null, null, null,
                        null, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL));
    }

    @Test
    void rechazaNotasDemasiadoLargas() {
        String longNotes = "a".repeat(1001);

        assertThrows(InvalidJournalEntryException.class, () ->
                JournalEntry.create(userId, "Radiohead", null,
                        ApproxDate.ofYear(2019), null, null, null,
                        longNotes, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL));
    }

    @Test
    void rechazaMasDeDiezCriterios() {
        List<RatingCriterion> criteria = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            criteria.add(RatingCriterion.of("criterio" + i, 3));
        }

        assertThrows(InvalidJournalEntryException.class, () ->
                JournalEntry.create(userId, "Radiohead", null,
                        ApproxDate.ofYear(2019), null, null, null,
                        null, criteria, null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL));
    }

    @Test
    void addMediaAcumulaFotosYVideosHastaElLimite() {
        JournalEntry entry = JournalEntry.create(userId, "Radiohead", null,
                ApproxDate.ofYear(2019), null, null, null,
                null, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);

        entry.addMedia(JournalMedia.photo(AssetId.of("journal/1"), 0));
        entry.addMedia(JournalMedia.video(AssetId.of("journal/clip"), 1, 10));

        assertEquals(2, entry.getMedia().size());
    }

    @Test
    void rechazaMasDeCincoVideos() {
        JournalEntry entry = JournalEntry.create(userId, "Radiohead", null,
                ApproxDate.ofYear(2019), null, null, null,
                null, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);

        for (int i = 0; i < 5; i++) {
            entry.addMedia(JournalMedia.video(AssetId.of("journal/clip" + i), i, 10));
        }

        assertThrows(TooManyMediaItemsException.class, () ->
                entry.addMedia(JournalMedia.video(AssetId.of("journal/clip6"), 5, 10)));
    }

    @Test
    void removeMediaEsIdempotente() {
        JournalEntry entry = JournalEntry.create(userId, "Radiohead", null,
                ApproxDate.ofYear(2019), null, null, null,
                null, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);
        JournalMedia photo = JournalMedia.photo(AssetId.of("journal/1"), 0);
        entry.addMedia(photo);

        entry.removeMedia(photo.id());
        entry.removeMedia(photo.id()); // no debe lanzar la segunda vez

        assertTrue(entry.getMedia().isEmpty());
    }

    @Test
    void makePublicYMakePrivateCambianLaVisibilidad() {
        JournalEntry entry = JournalEntry.create(userId, "Radiohead", null,
                ApproxDate.ofYear(2019), null, null, null,
                null, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);

        entry.makePublic();
        assertEquals(JournalVisibility.PUBLIC, entry.getVisibility());

        entry.makePrivate();
        assertEquals(JournalVisibility.PRIVATE, entry.getVisibility());
    }

    @Test
    void updateSustituyeElContenidoYMantieneLaMedia() {
        JournalEntry entry = JournalEntry.create(userId, "Radiohead", null,
                ApproxDate.ofYear(2019), null, null, null,
                null, List.of(), null, JournalVisibility.PRIVATE, JournalEntrySource.MANUAL);
        entry.addMedia(JournalMedia.photo(AssetId.of("journal/1"), 0));

        entry.update("Radiohead", null, ApproxDate.ofYear(2020), "Razzmatazz", "Barcelona",
                null, "Nuevas notas", List.of(RatingCriterion.of("sonido", 5)), null);

        assertEquals("Razzmatazz", entry.getVenueName());
        assertEquals(1, entry.getCriteria().size());
        assertFalse(entry.getMedia().isEmpty());
    }
}

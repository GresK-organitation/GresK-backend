package com.gresk.modules.journal.domain.model;

import com.gresk.modules.journal.domain.exception.InvalidJournalEntryException;
import com.gresk.shared.domain.valueobject.AssetId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JournalMediaTest {

    @Test
    void unaFotoNoRequiereDuracion() {
        JournalMedia media = JournalMedia.photo(AssetId.of("journal/abc"), 0);

        assertEquals(JournalMediaType.PHOTO, media.mediaType());
        assertEquals(null, media.durationSeconds());
    }

    @Test
    void unVideoDe15SegundosEsValido() {
        JournalMedia media = JournalMedia.video(AssetId.of("journal/clip"), 0, 15);

        assertEquals(JournalMediaType.VIDEO, media.mediaType());
        assertEquals(15, media.durationSeconds());
    }

    @Test
    void rechazaVideoDeMasDe15Segundos() {
        assertThrows(InvalidJournalEntryException.class,
                () -> JournalMedia.video(AssetId.of("journal/clip"), 0, 16));
    }

    @Test
    void rechazaVideoSinDuracion() {
        assertThrows(InvalidJournalEntryException.class,
                () -> new JournalMedia(JournalMediaId.generate(), JournalMediaType.VIDEO,
                        AssetId.of("journal/clip"), 0, null));
    }

    @Test
    void rechazaFotoConDuracion() {
        assertThrows(InvalidJournalEntryException.class,
                () -> new JournalMedia(JournalMediaId.generate(), JournalMediaType.PHOTO,
                        AssetId.of("journal/pic"), 0, 5));
    }

    @Test
    void rechazaAssetIdVacio() {
        assertThrows(InvalidJournalEntryException.class,
                () -> JournalMedia.photo(AssetId.of(""), 0));
    }
}

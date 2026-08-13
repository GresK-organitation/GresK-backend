package com.gresk.modules.curation.domain.model;

import com.gresk.modules.curation.domain.exception.DuplicateListItemException;
import com.gresk.modules.curation.domain.exception.InvalidCuratedListException;
import com.gresk.modules.curation.domain.exception.ListItemNotFoundException;
import com.gresk.modules.user.domain.model.UserId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CuratedListTest {

    private final UserId ownerId = UserId.of(UUID.randomUUID());

    private CuratedList newList() {
        return CuratedList.create(ownerId, "Mejores conciertos de jazz 2025", "BCN", ListVisibility.PRIVATE);
    }

    @Test
    void creaUnaListaVacia() {
        CuratedList list = newList();

        assertEquals("Mejores conciertos de jazz 2025", list.getTitle());
        assertTrue(list.getItems().isEmpty());
    }

    @Test
    void rechazaTituloVacio() {
        assertThrows(InvalidCuratedListException.class, () ->
                CuratedList.create(ownerId, "  ", null, ListVisibility.PRIVATE));
    }

    @Test
    void addItemAsignaPosicionesSecuenciales() {
        CuratedList list = newList();

        CuratedListItem first  = list.addItem(ListedEntryType.JOURNAL_ENTRY, UUID.randomUUID());
        CuratedListItem second = list.addItem(ListedEntryType.VERIFIED_REVIEW, UUID.randomUUID());

        assertEquals(0, first.position());
        assertEquals(1, second.position());
        assertEquals(2, list.getItems().size());
    }

    @Test
    void rechazaAnadirElMismoItemDosVeces() {
        CuratedList list = newList();
        UUID entryId = UUID.randomUUID();
        list.addItem(ListedEntryType.JOURNAL_ENTRY, entryId);

        assertThrows(DuplicateListItemException.class, () ->
                list.addItem(ListedEntryType.JOURNAL_ENTRY, entryId));
    }

    @Test
    void permiteElMismoEntryIdConDistintoTipo() {
        CuratedList list = newList();
        UUID entryId = UUID.randomUUID();
        list.addItem(ListedEntryType.JOURNAL_ENTRY, entryId);

        list.addItem(ListedEntryType.VERIFIED_REVIEW, entryId);

        assertEquals(2, list.getItems().size());
    }

    @Test
    void removeItemCompactaLasPosiciones() {
        CuratedList list = newList();
        CuratedListItem a = list.addItem(ListedEntryType.JOURNAL_ENTRY, UUID.randomUUID());
        CuratedListItem b = list.addItem(ListedEntryType.JOURNAL_ENTRY, UUID.randomUUID());
        list.addItem(ListedEntryType.JOURNAL_ENTRY, UUID.randomUUID());

        list.removeItem(a.id());

        List<CuratedListItem> remaining = list.getItems();
        assertEquals(2, remaining.size());
        assertEquals(b.id(), remaining.get(0).id());
        assertEquals(0, remaining.get(0).position());
        assertEquals(1, remaining.get(1).position());
    }

    @Test
    void removeItemInexistenteLanzaExcepcion() {
        CuratedList list = newList();

        assertThrows(ListItemNotFoundException.class, () ->
                list.removeItem(CuratedListItemId.generate()));
    }

    @Test
    void reorderReasignaLasPosicionesSegunElOrdenDado() {
        CuratedList list = newList();
        CuratedListItem a = list.addItem(ListedEntryType.JOURNAL_ENTRY, UUID.randomUUID());
        CuratedListItem b = list.addItem(ListedEntryType.JOURNAL_ENTRY, UUID.randomUUID());

        list.reorder(List.of(b.id(), a.id()));

        assertEquals(b.id(), list.getItems().get(0).id());
        assertEquals(0, list.getItems().get(0).position());
        assertEquals(a.id(), list.getItems().get(1).id());
        assertEquals(1, list.getItems().get(1).position());
    }

    @Test
    void reorderConIdsIncompletosLanzaExcepcion() {
        CuratedList list = newList();
        CuratedListItem a = list.addItem(ListedEntryType.JOURNAL_ENTRY, UUID.randomUUID());
        list.addItem(ListedEntryType.JOURNAL_ENTRY, UUID.randomUUID());

        assertThrows(InvalidCuratedListException.class, () -> list.reorder(List.of(a.id())));
    }
}

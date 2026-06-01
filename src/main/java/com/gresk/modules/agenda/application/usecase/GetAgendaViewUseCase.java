package com.gresk.modules.agenda.application.usecase;

import com.gresk.modules.agenda.application.dto.AgendaResponseMapper;
import com.gresk.modules.agenda.application.dto.AgendaViewItemResponse;
import com.gresk.modules.agenda.application.dto.AgendaViewResponse;
import com.gresk.modules.agenda.application.query.AgendaViewQuery;
import com.gresk.modules.agenda.domain.model.AgendaEntry;
import com.gresk.modules.agenda.domain.model.AgendaGresKEvent;
import com.gresk.modules.agenda.domain.port.out.AgendaEntryRepository;
import com.gresk.modules.agenda.domain.port.out.PromoterEventQueryPort;
import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAgendaViewUseCase {

    private final AgendaEntryRepository  agendaRepository;
    private final PromoterEventQueryPort eventQueryPort;
    private final AgendaResponseMapper   mapper;

    public AgendaViewResponse execute(AgendaViewQuery query) {
        PromoterId promoterId = PromoterId.of(query.promoterId());
        Instant    from       = query.from();
        Instant    to         = query.to();
        Set<String> types     = query.types();

        List<AgendaViewItemResponse> items = new ArrayList<>();

        // ── Entradas simples de agenda ────────────────────────────────────────
        boolean includeAgenda = isEmpty(types) || types.stream()
                .anyMatch(t -> t.equals("TASK") || t.equals("APPOINTMENT") || t.equals("REMINDER"));

        if (includeAgenda) {
            List<AgendaEntry> simple = agendaRepository.findSimpleByPromoterAndDateRange(promoterId, from, to);
            simple.stream()
                    .filter(e -> isEmpty(types) || types.contains(e.getType().name()))
                    .map(mapper::toViewItem)
                    .forEach(items::add);

            // ── Entradas recurrentes con expansión ────────────────────────────
            List<AgendaEntry> masters = agendaRepository.findMastersWithPotentialOccurrences(promoterId, from, to);
            for (AgendaEntry master : masters) {
                if (!isEmpty(types) && !types.contains(master.getType().name())) continue;

                List<Instant> occurrences = master.getRecurrenceRule().expand(master.getStartAt(), from, to);

                // Cargar excepciones de esta serie indexadas por exception_date
                Map<Instant, AgendaEntry> exceptions = agendaRepository
                        .findExceptionsBySeriesId(master.getId())
                        .stream()
                        .collect(Collectors.toMap(AgendaEntry::getExceptionDate, e -> e));

                for (Instant occ : occurrences) {
                    AgendaEntry exception = exceptions.get(occ);
                    if (exception != null && exception.isCancelled()) continue; // ocurrencia cancelada
                    if (exception != null) {
                        items.add(mapper.toViewItem(exception));  // ocurrencia sobreescrita
                    } else {
                        items.add(mapper.toViewItemWithDate(master, occ.toString())); // ocurrencia estándar
                    }
                }
            }
        }

        // ── Eventos GresK ─────────────────────────────────────────────────────
        if (isEmpty(types) || types.contains("GRESK_EVENT")) {
            List<AgendaGresKEvent> gresKEvents = eventQueryPort.findByPromoterAndDateRange(promoterId, from, to);
            gresKEvents.stream()
                    .map(mapper::toViewItemFromGresK)
                    .forEach(items::add);
        }

        // ── Ordenar por startAt ───────────────────────────────────────────────
        items.sort(Comparator.comparing(
                AgendaViewItemResponse::startAt,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        return new AgendaViewResponse(items);
    }

    private boolean isEmpty(Set<String> types) {
        return types == null || types.isEmpty();
    }
}

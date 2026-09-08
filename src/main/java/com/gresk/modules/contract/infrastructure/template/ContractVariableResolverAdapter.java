package com.gresk.modules.contract.infrastructure.template;

import com.gresk.modules.artist.domain.model.Artist;
import com.gresk.modules.artist.domain.model.valueobject.ArtistId;
import com.gresk.modules.artist.domain.port.out.ArtistRepositoryPort;
import com.gresk.modules.contract.domain.model.Contract;
import com.gresk.modules.contract.domain.model.valueobject.ContractParty;
import com.gresk.modules.contract.domain.model.valueobject.FinancialTerms;
import com.gresk.modules.contract.domain.model.valueobject.PerformanceDetails;
import com.gresk.modules.contract.domain.model.valueobject.WithholdingTax;
import com.gresk.modules.contract.domain.port.out.ContractVariableResolverPort;
import com.gresk.modules.event.domain.model.Event;
import com.gresk.modules.event.domain.model.EventId;
import com.gresk.modules.event.domain.port.out.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Cruza los módulos event/artist inyectando directamente sus puertos de salida — mismo
 * patrón ya usado por el módulo rider (CreateRiderUseCase/LinkRiderToEventUseCase), sin
 * ACL/facade intermedio.
 */
@Component
@RequiredArgsConstructor
public class ContractVariableResolverAdapter implements ContractVariableResolverPort {

    private final EventRepository       eventRepository;
    private final ArtistRepositoryPort  artistRepository;

    @Override
    public Map<String, Object> resolveVariables(Contract contract) {
        Map<String, Object> root = new HashMap<>();
        root.put("contract", contractVars(contract));
        root.put("partyA", partyVars(contract.getPartyA()));
        root.put("partyB", partyVars(contract.getPartyB()));
        root.put("performance", performanceVars(contract.getPerformanceDetails()));
        root.put("financial", financialVars(contract.getFinancialTerms()));

        if (contract.getLinkedEventId() != null) {
            eventRepository.findById(new EventId(contract.getLinkedEventId()))
                    .ifPresent(event -> root.put("event", eventVars(event)));
        }
        if (contract.getLinkedArtistId() != null) {
            artistRepository.findById(ArtistId.of(contract.getLinkedArtistId()))
                    .ifPresent(artist -> root.put("artist", artistVars(artist)));
        }
        return root;
    }

    private Map<String, Object> contractVars(Contract c) {
        Map<String, Object> m = new HashMap<>();
        m.put("referenceNumber", c.getReferenceNumber());
        m.put("type", c.getType());
        m.put("jurisdiction", nullToEmpty(c.getJurisdiction()));
        m.put("contractCity", nullToEmpty(c.getContractCity()));
        m.put("contractDate", c.getContractDate() != null ? c.getContractDate().toString() : "");
        return m;
    }

    private Map<String, Object> partyVars(ContractParty p) {
        Map<String, Object> m = new HashMap<>();
        if (p == null) return m;
        m.put("name", nullToEmpty(p.name()));
        m.put("taxId", nullToEmpty(p.taxId()));
        m.put("address", nullToEmpty(p.address()));
        m.put("country", nullToEmpty(p.country()));
        m.put("signatoryName", nullToEmpty(p.signatoryName()));
        m.put("signatoryRole", nullToEmpty(p.signatoryRole()));
        m.put("email", nullToEmpty(p.email()));
        return m;
    }

    private Map<String, Object> performanceVars(PerformanceDetails pd) {
        Map<String, Object> m = new HashMap<>();
        if (pd == null) return m;
        m.put("venue", nullToEmpty(pd.venue()));
        m.put("eventDate", pd.eventDate() != null ? pd.eventDate().toString() : "");
        m.put("durationMinutes", pd.durationMinutes() != null ? pd.durationMinutes() : "");
        m.put("showTime", nullToEmpty(pd.showTime()));
        return m;
    }

    private Map<String, Object> financialVars(FinancialTerms ft) {
        Map<String, Object> m = new HashMap<>();
        if (ft == null) return m;
        m.put("feeAmount", ft.feeAmount() != null ? ft.feeAmount().toPlainString() : "");
        m.put("feeCurrency", nullToEmpty(ft.feeCurrency()));
        WithholdingTax wht = ft.withholdingTax();
        Map<String, Object> whtVars = new HashMap<>();
        whtVars.put("type", wht != null ? wht.type() : "NONE");
        whtVars.put("ratePercentage", wht != null && wht.ratePercentage() != null ? wht.ratePercentage().toPlainString() : "0");
        whtVars.put("withheldAmount", wht != null && wht.withheldAmount() != null ? wht.withheldAmount().toPlainString() : "0");
        m.put("withholdingTax", whtVars);
        return m;
    }

    private Map<String, Object> eventVars(Event event) {
        Map<String, Object> m = new HashMap<>();
        m.put("title", nullToEmpty(event.getTitle()));
        m.put("venue", event.getLocation() != null ? nullToEmpty(event.getLocation().venue()) : "");
        m.put("capacityTotal", event.getCapacity() != null ? event.getCapacity().total() : "");
        m.put("capacityAvailable", event.getCapacity() != null ? event.getCapacity().available() : "");
        m.put("priceAmount", event.getPrice() != null ? event.getPrice().amount().toPlainString() : "");
        m.put("priceCurrency", event.getPrice() != null ? event.getPrice().currency() : "");
        m.put("genre", event.getGenre() != null ? event.getGenre().toString() : "");
        m.put("eventDate", event.getEventDate() != null ? event.getEventDate().toString() : "");
        return m;
    }

    private Map<String, Object> artistVars(Artist artist) {
        Map<String, Object> m = new HashMap<>();
        m.put("name", artist.getName() != null ? artist.getName().value() : "");
        m.put("origin", artist.getOrigin() != null ? artist.getOrigin().value() : "");
        return m;
    }

    private String nullToEmpty(String s) { return s != null ? s : ""; }
}

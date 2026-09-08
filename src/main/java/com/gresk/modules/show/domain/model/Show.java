package com.gresk.modules.show.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.show.domain.exception.IncompleteShowException;
import com.gresk.modules.show.domain.exception.InvalidShowStatusTransitionException;
import com.gresk.modules.show.domain.exception.ShowAlreadySettledException;
import com.gresk.modules.show.domain.exception.VenueHoldExpiredException;
import com.gresk.modules.show.domain.model.valueobject.FinancialSimulation;
import com.gresk.modules.show.domain.model.valueobject.FinancialViabilityReport;
import com.gresk.modules.show.domain.model.valueobject.HoldWindow;
import com.gresk.modules.show.domain.model.valueobject.SettlementSummary;
import com.gresk.modules.show.domain.model.valueobject.VenueBooking;

import java.time.Instant;
import java.util.UUID;

/**
 * Aggregate root de la producción de un evento (el "Módulo de Eventos" de la promotora).
 * Es deliberadamente distinto del {@code Event} del módulo {@code event}: aquel es el listado
 * público de venta de entradas; este es el expediente operativo interno -- máquina de estados,
 * ficha de recinto elegida y P&amp;L -- que en algún punto de su ciclo de vida (transición
 * {@link #openSales(String)}) *produce* ese listado a través de un puerto anti-corrupción.
 *
 * La bitácora ({@code ShowLogEntry}) vive en su propio repositorio (mismo patrón que
 * {@code contract.AuditTrailEntry} respecto a {@code Contract}), no como colección cargada
 * dentro de este agregado: evita que el aggregate crezca sin límite con el histórico de
 * comunicaciones de shows con años de trazabilidad.
 */
public final class Show {

    private final ShowId      id;
    private final PromoterId  promoterId;
    private final Instant     createdAt;

    private String              name;
    private Instant              scheduledDate;
    private VenueBooking         venueBooking;             // null hasta seleccionar recinto+aforo
    private HoldWindow           holdWindow;               // solo relevante en OPCION_HOLD
    private FinancialSimulation  financialSimulation;      // input del P&L borrador, recalculable
    private ShowStatus           status;
    private SettlementSummary    settlement;               // solo tras LIQUIDADO
    private UUID                 linkedMarketplaceEventId; // fijado en EN_VENTA
    private String               cancellationReason;
    private Instant              updatedAt;

    private Show(ShowId id, PromoterId promoterId, Instant createdAt, String name, Instant scheduledDate,
                 VenueBooking venueBooking, HoldWindow holdWindow, FinancialSimulation financialSimulation,
                 ShowStatus status, SettlementSummary settlement, UUID linkedMarketplaceEventId,
                 String cancellationReason, Instant updatedAt) {
        this.id                       = id;
        this.promoterId               = promoterId;
        this.createdAt                = createdAt;
        this.name                     = name;
        this.scheduledDate            = scheduledDate;
        this.venueBooking             = venueBooking;
        this.holdWindow               = holdWindow;
        this.financialSimulation      = financialSimulation;
        this.status                   = status;
        this.settlement               = settlement;
        this.linkedMarketplaceEventId = linkedMarketplaceEventId;
        this.cancellationReason       = cancellationReason;
        this.updatedAt                = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static Show create(PromoterId promoterId, String name, Instant tentativeDate) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Show name must not be blank");
        }
        Instant now = Instant.now();
        return new Show(ShowId.generate(), promoterId, now, name, tentativeDate,
                null, null, null, ShowStatus.BORRADOR, null, null, null, now);
    }

    public static Show reconstitute(ShowId id, PromoterId promoterId, Instant createdAt, String name,
                                     Instant scheduledDate, VenueBooking venueBooking, HoldWindow holdWindow,
                                     FinancialSimulation financialSimulation, ShowStatus status,
                                     SettlementSummary settlement, UUID linkedMarketplaceEventId,
                                     String cancellationReason, Instant updatedAt) {
        return new Show(id, promoterId, createdAt, name, scheduledDate, venueBooking, holdWindow,
                financialSimulation, status, settlement, linkedMarketplaceEventId, cancellationReason, updatedAt);
    }

    // ── Edición (solo en estados editables) ─────────────────────────────────

    public void updateDetails(String name, Instant scheduledDate) {
        requireEditable();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Show name must not be blank");
        }
        this.name = name;
        this.scheduledDate = scheduledDate;
        touch();
    }

    public void selectVenue(VenueBooking venueBooking) {
        requireEditable();
        if (venueBooking == null) {
            throw new IllegalArgumentException("venueBooking must not be null");
        }
        this.venueBooking = venueBooking;
        touch();
    }

    /** Recalcula el P&amp;L borrador con nuevos supuestos de coste/ingreso. Idempotente y sin efectos colaterales. */
    public FinancialViabilityReport runViabilitySimulation(FinancialSimulation simulation) {
        requireEditable();
        if (venueBooking == null) {
            throw new IncompleteShowException("Cannot simulate viability: no venue/capacity selected yet");
        }
        this.financialSimulation = simulation;
        touch();
        return simulation.calculateBreakEven(venueBooking.confirmedCapacity());
    }

    // ── Máquina de estados ───────────────────────────────────────────────────

    public void placeHold(HoldWindow holdWindow) {
        requireTransition(ShowStatus.OPCION_HOLD);
        if (venueBooking == null) {
            throw new IncompleteShowException("Cannot place a hold without a selected venue/capacity");
        }
        if (holdWindow == null) {
            throw new IllegalArgumentException("holdWindow must not be null");
        }
        this.status = ShowStatus.OPCION_HOLD;
        this.holdWindow = holdWindow;
        touch();
    }

    /** Liberación voluntaria del hold: el show vuelve a BORRADOR sin perder el resto de datos. */
    public void releaseHold() {
        requireTransition(ShowStatus.BORRADOR);
        this.status = ShowStatus.BORRADOR;
        this.holdWindow = null;
        touch();
    }

    /** Invocado por el scheduler cuando {@code holdWindow} ha caducado sin confirmación. */
    public void expireHold() {
        requireTransition(ShowStatus.BORRADOR);
        this.status = ShowStatus.BORRADOR;
        this.venueBooking = null;
        this.holdWindow = null;
        touch();
    }

    public void confirm() {
        requireTransition(ShowStatus.CONFIRMADO);
        assertReadyToConfirm();
        this.status = ShowStatus.CONFIRMADO;
        this.holdWindow = null;
        touch();
    }

    /**
     * Abre la venta. No publica el listado de marketplace: solo marca el show como listo para
     * ello y guarda el id devuelto por el puerto anti-corrupción una vez el caso de uso lo invoque.
     */
    public void openSales(UUID linkedMarketplaceEventId) {
        requireTransition(ShowStatus.EN_VENTA);
        if (linkedMarketplaceEventId == null) {
            throw new IllegalArgumentException("linkedMarketplaceEventId must not be null");
        }
        this.status = ShowStatus.EN_VENTA;
        this.linkedMarketplaceEventId = linkedMarketplaceEventId;
        touch();
    }

    public void startExecution() {
        requireTransition(ShowStatus.EN_EJECUCION);
        this.status = ShowStatus.EN_EJECUCION;
        touch();
    }

    public void finish() {
        requireTransition(ShowStatus.FINALIZADO);
        this.status = ShowStatus.FINALIZADO;
        touch();
    }

    public void settle(SettlementSummary settlement) {
        if (this.status == ShowStatus.LIQUIDADO) {
            throw new ShowAlreadySettledException(id.toString());
        }
        requireTransition(ShowStatus.LIQUIDADO);
        this.status = ShowStatus.LIQUIDADO;
        this.settlement = settlement;
        touch();
    }

    public void cancel(String reason) {
        requireTransition(ShowStatus.CANCELADO);
        this.status = ShowStatus.CANCELADO;
        this.cancellationReason = reason;
        touch();
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    private void requireEditable() {
        if (!status.isEditable()) {
            throw new InvalidShowStatusTransitionException(status, status);
        }
    }

    private void requireTransition(ShowStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidShowStatusTransitionException(status, target);
        }
    }

    private void assertReadyToConfirm() {
        if (name == null || name.isBlank()) {
            throw new IncompleteShowException("Cannot confirm show: name is missing");
        }
        if (scheduledDate == null) {
            throw new IncompleteShowException("Cannot confirm show: scheduledDate is missing");
        }
        if (venueBooking == null) {
            throw new IncompleteShowException("Cannot confirm show: venue/capacity is missing");
        }
        if (status == ShowStatus.OPCION_HOLD && holdWindow != null && holdWindow.isExpired(Instant.now())) {
            throw new VenueHoldExpiredException(id.toString());
        }
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public ShowId               getId()                       { return id; }
    public PromoterId           getPromoterId()                { return promoterId; }
    public Instant              getCreatedAt()                 { return createdAt; }
    public String               getName()                      { return name; }
    public Instant              getScheduledDate()             { return scheduledDate; }
    public VenueBooking         getVenueBooking()              { return venueBooking; }
    public HoldWindow           getHoldWindow()                { return holdWindow; }
    public FinancialSimulation  getFinancialSimulation()       { return financialSimulation; }
    public ShowStatus           getStatus()                    { return status; }
    public SettlementSummary    getSettlement()                { return settlement; }
    public UUID                 getLinkedMarketplaceEventId()  { return linkedMarketplaceEventId; }
    public String               getCancellationReason()        { return cancellationReason; }
    public Instant              getUpdatedAt()                 { return updatedAt; }
}

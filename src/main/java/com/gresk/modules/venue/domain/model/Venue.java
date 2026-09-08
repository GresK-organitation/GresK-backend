package com.gresk.modules.venue.domain.model;

import com.gresk.modules.promoter.domain.model.valueobject.PromoterId;
import com.gresk.modules.venue.domain.exception.CapacityConfigurationNotFoundException;
import com.gresk.modules.venue.domain.exception.DuplicateCapacityConfigurationException;
import com.gresk.modules.venue.domain.model.valueobject.CapacityConfiguration;
import com.gresk.modules.venue.domain.model.valueobject.CurfewPolicy;
import com.gresk.modules.venue.domain.model.valueobject.EvacuationPlan;
import com.gresk.modules.venue.domain.model.valueobject.LoadingDockSpec;
import com.gresk.modules.venue.domain.model.valueobject.MunicipalLicense;
import com.gresk.shared.domain.valueobject.Address;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Ficha técnica de un recinto. Es la fuente de verdad que {@code Show} consulta (vía
 * {@code VenueTechnicalFileQueryPort}) al elegir aforo o al confirmar una fecha: ni las
 * licencias ni los aforos modulares se copian aquí desde ningún otro sitio, se gestionan
 * directamente sobre este agregado.
 */
public final class Venue {

    private final VenueId    id;
    private final PromoterId ownerId;
    private final Instant    createdAt;

    private String                     name;
    private Address                    address;
    private List<CapacityConfiguration> capacityConfigurations;
    private CurfewPolicy               curfewPolicy;
    private EvacuationPlan             evacuationPlan;
    private List<MunicipalLicense>     licenses;
    private LoadingDockSpec            loadingDockSpec;
    private boolean                    active;
    private Instant                    updatedAt;

    private Venue(VenueId id, PromoterId ownerId, Instant createdAt, String name, Address address,
                  List<CapacityConfiguration> capacityConfigurations, CurfewPolicy curfewPolicy,
                  EvacuationPlan evacuationPlan, List<MunicipalLicense> licenses,
                  LoadingDockSpec loadingDockSpec, boolean active, Instant updatedAt) {
        this.id                     = id;
        this.ownerId                = ownerId;
        this.createdAt              = createdAt;
        this.name                   = name;
        this.address                = address;
        this.capacityConfigurations = capacityConfigurations != null ? new ArrayList<>(capacityConfigurations) : new ArrayList<>();
        this.curfewPolicy           = curfewPolicy;
        this.evacuationPlan         = evacuationPlan;
        this.licenses               = licenses != null ? new ArrayList<>(licenses) : new ArrayList<>();
        this.loadingDockSpec        = loadingDockSpec;
        this.active                 = active;
        this.updatedAt              = updatedAt;
    }

    // ── Factories ────────────────────────────────────────────────────────────

    public static Venue register(PromoterId ownerId, String name, Address address) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Venue name must not be blank");
        }
        if (address == null) {
            throw new IllegalArgumentException("Venue address must not be null");
        }
        Instant now = Instant.now();
        return new Venue(VenueId.generate(), ownerId, now, name, address,
                List.of(), null, null, List.of(), null, true, now);
    }

    public static Venue reconstitute(VenueId id, PromoterId ownerId, Instant createdAt, String name, Address address,
                                      List<CapacityConfiguration> capacityConfigurations, CurfewPolicy curfewPolicy,
                                      EvacuationPlan evacuationPlan, List<MunicipalLicense> licenses,
                                      LoadingDockSpec loadingDockSpec, boolean active, Instant updatedAt) {
        return new Venue(id, ownerId, createdAt, name, address, capacityConfigurations, curfewPolicy,
                evacuationPlan, licenses, loadingDockSpec, active, updatedAt);
    }

    // ── Behavior ─────────────────────────────────────────────────────────────

    public void addCapacityConfiguration(CapacityConfiguration configuration) {
        boolean exists = capacityConfigurations.stream().anyMatch(c -> c.code().equals(configuration.code()));
        if (exists) {
            throw new DuplicateCapacityConfigurationException(configuration.code());
        }
        capacityConfigurations.add(configuration);
        touch();
    }

    public void removeCapacityConfiguration(String code) {
        boolean removed = capacityConfigurations.removeIf(c -> c.code().equals(code));
        if (!removed) {
            throw new CapacityConfigurationNotFoundException(code);
        }
        touch();
    }

    public CapacityConfiguration requireCapacityConfiguration(String code) {
        return capacityConfigurations.stream()
                .filter(c -> c.code().equals(code))
                .findFirst()
                .orElseThrow(() -> new CapacityConfigurationNotFoundException(code));
    }

    public void updateCurfewPolicy(CurfewPolicy curfewPolicy) {
        this.curfewPolicy = curfewPolicy;
        touch();
    }

    public void updateEvacuationPlan(EvacuationPlan evacuationPlan) {
        this.evacuationPlan = evacuationPlan;
        touch();
    }

    public void updateLoadingDockSpec(LoadingDockSpec loadingDockSpec) {
        this.loadingDockSpec = loadingDockSpec;
        touch();
    }

    /** Añade o sustituye (mismo tipo) una licencia municipal vigente. */
    public void registerLicense(MunicipalLicense license) {
        licenses.removeIf(l -> l.type() == license.type());
        licenses.add(license);
        touch();
    }

    public Optional<MunicipalLicense> licenseOf(com.gresk.modules.venue.domain.model.valueobject.LicenseType type) {
        return licenses.stream().filter(l -> l.type() == type).findFirst();
    }

    public void activate() {
        this.active = true;
        touch();
    }

    public void deactivate() {
        this.active = false;
        touch();
    }

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Venue name must not be blank");
        }
        this.name = name;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public VenueId                      getId()                     { return id; }
    public PromoterId                   getOwnerId()                { return ownerId; }
    public Instant                      getCreatedAt()               { return createdAt; }
    public String                       getName()                   { return name; }
    public Address                      getAddress()                { return address; }
    public List<CapacityConfiguration>  getCapacityConfigurations() { return List.copyOf(capacityConfigurations); }
    public CurfewPolicy                 getCurfewPolicy()           { return curfewPolicy; }
    public EvacuationPlan               getEvacuationPlan()         { return evacuationPlan; }
    public List<MunicipalLicense>       getLicenses()               { return List.copyOf(licenses); }
    public LoadingDockSpec              getLoadingDockSpec()        { return loadingDockSpec; }
    public boolean                      isActive()                  { return active; }
    public Instant                      getUpdatedAt()              { return updatedAt; }
}
